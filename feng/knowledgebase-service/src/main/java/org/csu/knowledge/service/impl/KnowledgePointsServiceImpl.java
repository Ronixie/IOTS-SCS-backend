package org.csu.knowledge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.exception.AssignmentException;
import org.csu.knowledge.clients.UserClient;
import org.csu.knowledge.entity.dto.KnowledgeDTO;
import org.csu.knowledge.entity.po.KnowledgePoints;
import org.csu.knowledge.entity.po.Comment;
import org.csu.knowledge.entity.po.UserLike;
import org.csu.knowledge.entity.po.UserFavorite;
import org.csu.knowledge.entity.po.UserHistory;
import org.csu.knowledge.entity.vo.*;
import org.csu.knowledge.enums.KnowledgePointStatus;
import org.csu.knowledge.mapper.KnowledgePointsMapper;
import org.csu.knowledge.mapper.CommentMapper;
import org.csu.knowledge.mapper.UserLikeMapper;
import org.csu.knowledge.mapper.UserFavoriteMapper;
import org.csu.knowledge.mapper.UserHistoryMapper;
import org.csu.knowledge.service.IKnowledgePointsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2025-07-05
 */
@Service
@RequiredArgsConstructor
@Slf4j
/*
  知识库服务实现类
 */
public class KnowledgePointsServiceImpl extends ServiceImpl<KnowledgePointsMapper, KnowledgePoints> implements IKnowledgePointsService {
    private final RestHighLevelClient client;
    private final UserClient userClient;
    private final RedisTemplate<String,Object> redisTemplate;
    private final CommentMapper commentMapper;
    private final UserLikeMapper userLikeMapper;
    private final UserFavoriteMapper userFavoriteMapper;
    private final UserHistoryMapper userHistoryMapper;
    @Value("${csu.knowledge.index-name}")
    private String indexName;
    @Value("${file.store.path}")
    private String rootDir;

    /**
     * 浏览知识库列表
     * @param sort 排序方式(1:创建时间 2:更新时间)
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param createUid 创建用户ID
     * @param userId 当前用户ID
     * @return 知识库列表VO
     */
    @Override
    public List<KnowledgeListVO> browseKnowledge(int sort, int page, int size, String keyword, Long createUid,long userId,String status) {
        boolean isSearch = keyword != null && !keyword.isEmpty();
        
        // 如果是搜索操作，直接从Elasticsearch查询
        if (isSearch) {
            return _searchFromElasticsearch(sort, page, size, keyword, createUid, userId, status);
        }
        
        // 非搜索操作，尝试从Redis获取缓存
        String cacheKey = _generateCacheKey(sort, page, size, createUid, userId,status);
        List<KnowledgeListVO> cachedResult = (List<KnowledgeListVO>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedResult != null) {
            log.info("从Redis缓存获取知识库列表，缓存键: {}", cacheKey);
            return cachedResult;
        }
        
        // Redis中没有缓存，从MySQL查询并缓存到Redis
        log.info("Redis缓存未命中，从MySQL查询知识库列表");
        List<KnowledgeListVO> result = _queryFromMySQL(sort, page, size, createUid, userId, status);
        
        // 将结果缓存到Redis，设置过期时间为30分钟
        redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
        log.info("知识库列表已缓存到Redis，缓存键: {}", cacheKey);
        
        return result;
    }

    /**
     * 获取知识库详情
     * @param kpId 知识库ID
     * @param userId 当前用户ID
     * @return 知识库实体
     */
    @Override
    public KnowledgeDetailVO getKnowledge(Long kpId, long userId) {
        KnowledgePoints knowledge = getById(kpId);
        if (knowledge == null) {
            throw new RuntimeException("该知识库不存在");
        }
        if (Objects.equals(knowledge.getStatus(), KnowledgePointStatus.DRAFT.getValue()) && knowledge.getAuthorId() != userId) {
            throw new RuntimeException("越权访问");
        }
        KnowledgeDetailVO vo = BeanUtil.copyProperties(knowledge, KnowledgeDetailVO.class);
        UserVO user = userClient.getUserInfo(knowledge.getAuthorId()).getData();
        KnowledgeListVO.Creator creator = new KnowledgeListVO.Creator();
        creator.setId(user.getUid());
        creator.setName(user.getName());
        creator.setAvatar(user.getAvatorUrl());
        vo.setCreator(creator);
        
        // 获取点赞数、评论数、收藏数
        vo.setLikeCount(userLikeMapper.countByKpId(kpId));
        vo.setCommentCount(commentMapper.countByKpId(kpId));
        vo.setFavoriteCount(userFavoriteMapper.countByKpId(kpId));
        
        // 检查当前用户是否已点赞、收藏
        vo.setIsLiked(userLikeMapper.selectByKpIdAndUserId(kpId, userId) != null);
        vo.setIsFavorited(userFavoriteMapper.selectByKpIdAndUserId(kpId, userId) != null);
        
        return vo;
    }

    /**
     * 添加或更新知识库
     * @param dto 知识库DTO
     * @param files 附件文件
     * @param userId 当前用户ID
     * @param isDraft 是否为草稿
     */
    @Override
    public Long addOrUpdateKnowledge(KnowledgeDTO dto, MultipartFile[] files, long userId, boolean isDraft) {
        Long kpId = dto.getKpId();
        KnowledgePoints knowledge = null;
        String originAttachmentsJson = null;
        if (kpId != null) {
            knowledge = getById(kpId);
            if (knowledge.getAuthorId() != userId) {
                throw new RuntimeException("越权访问");
            }
            originAttachmentsJson = knowledge.getAttachmentsJson();
        }
        String[] attachments;
        if (dto.getAttachmentsJson() != null && !dto.getAttachmentsJson().isEmpty()) {
            attachments = JSON.parseObject(dto.getAttachmentsJson(), String[].class);
        } else {
            attachments = new String[0];
        }
        if (originAttachmentsJson != null) {
            String[] originAttachments = JSON.parseObject(originAttachmentsJson, String[].class);
            String[] removeAttachments = _removeElements(originAttachments, attachments);
            _removeFile(removeAttachments);
        }
        // 保存文件
        String[] paths = _saveFile(files);
        String[] finalAttachments = Stream.concat(Arrays.stream(attachments), Arrays.stream(paths))
                .toArray(String[]::new);
        // 构建最终保存实体
        KnowledgePoints finalKnowledge = BeanUtil.copyProperties(dto, KnowledgePoints.class);
        finalKnowledge.setAttachmentsJson(JSON.toJSONString(finalAttachments));
        finalKnowledge.setAuthorId(userId);
        if (isDraft) {
            finalKnowledge.setStatus(KnowledgePointStatus.DRAFT.getValue());
        } else {
            finalKnowledge.setStatus(KnowledgePointStatus.PUBLISHED.getValue());
        }
        if (knowledge != null) {
            finalKnowledge.setCreatedAt(knowledge.getCreatedAt());
            finalKnowledge.setUpdatedAt(LocalDateTime.now());
        }else{
            finalKnowledge.setKpId(_generateId());
            finalKnowledge.setCreatedAt(LocalDateTime.now());
            finalKnowledge.setUpdatedAt(LocalDateTime.now());
        }
        UserVO vo = userClient.getUserInfo(userId).getData();
        // 插入或删除索引库
        try{
            if(knowledge!=null){
                _updateIntoIndex(finalKnowledge,vo);
            }else{
                _insertIntoIndex(finalKnowledge,vo);
            }
        }catch(IOException e) {
            log.error("插入/更新索引库失败");
            throw new RuntimeException("插入/更新索引库失败");
        }
        saveOrUpdate(finalKnowledge);
        
        // 清除相关缓存
        _clearKnowledgeCache();
        return finalKnowledge.getKpId();
    }

    /**
     * 删除知识库
     * @param kpId 知识库ID
     * @param userId 当前用户ID
     */
    @Override
    public void deleteKnowledge(Long kpId, long userId) {
        KnowledgePoints knowledge = getById(kpId);
        if (knowledge.getAuthorId() != userId) {
            throw new RuntimeException("越权访问");
        }
        // 删除索引库
        try{
            _deleteFromIndex(kpId);
        } catch (IOException e) {
            log.error("删除索引库失败");
            throw new RuntimeException("删除索引库失败");
        }
        removeById(kpId);
        
        // 清除相关缓存
        _clearKnowledgeCache();
    }

    /**
     * 更新知识库状态
     * @param id 知识库ID
     * @param status 状态
     * @param userId 当前用户ID
     */
    @Override
    public void updateStatus(Long id, String status, long userId) {
        KnowledgePoints knowledge = getById(id);
        if (knowledge == null) {
            throw new RuntimeException("该知识库不存在");
        }
        if(knowledge.getAuthorId()!= userId){
            throw new RuntimeException("越权访问");
        }
        try{
            _updateStatus(id,status);
        } catch (IOException e) {
            log.error("更新索引库状态失败");
            throw new RuntimeException("更新索引库状态失败");
        }
        knowledge.setStatus(status);
        updateById(knowledge);
        
        // 清除相关缓存
        _clearKnowledgeCache();
    }

    @Override
    public File download(Long kpId, long userId, String attachment) {
        KnowledgePoints knowledge = getById(kpId);
        if (knowledge == null) {
            throw new RuntimeException("该知识库不存在");
        }
        if (Objects.equals(knowledge.getStatus(), KnowledgePointStatus.DRAFT.getValue()) && knowledge.getAuthorId() != userId) {
            throw new RuntimeException("越权访问");
        }
        String[] attachments = JSON.parseObject(knowledge.getAttachmentsJson(), String[].class);
        if (attachments == null || attachments.length == 0) {
            throw new RuntimeException("该知识库没有附件");
        }
        if (attachment == null || !Arrays.asList(attachments).contains(attachment)) {
            throw new RuntimeException("该知识库没有该附件");
        }
        File file = new File(rootDir + File.separator + attachment);
        if (!file.exists()) {
            throw new RuntimeException("该附件不存在");
        }
        return file;
    }

    /**
     * 移除元素
     * @param source 源数组
     * @param toRemove 待移除元素
     * @return 移除后的数组
     */
    private String[] _removeElements(String[] source, String[] toRemove) {
        Set<String> removeSet = new HashSet<>(Arrays.asList(toRemove));
        List<String> resultList = new ArrayList<>();
        for (String element : source) {
            if (!removeSet.contains(element)) {
                resultList.add(element);
            }
        }
        // 将结果转换为数组
        return resultList.toArray(new String[0]);
    }

    /**
     * 保存上传的文件到指定目录
     * @param files 上传的文件数组
     * @return 保存后的文件路径数组
     */
    private String[] _saveFile(MultipartFile[] files) {
        if (files == null)
            return new String[0];
        int size = files.length;
        String[] filePath = new String[size];
        int index = 0;
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                // 获取文件名
                String fileName = file.getOriginalFilename();
                // 生成目录名
                String dirName = LocalDateTime.now().toString().replace(":", "-");
                // 确保目录存在
                File dir = new File(rootDir + File.separator + dirName);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                if (fileName == null) {
                    fileName = UUID.randomUUID().toString();
                }
                filePath[index++] = dirName + File.separator + fileName;
                // 保存文件
                file.transferTo(new File(dir, fileName));
            } catch (Exception e) {
                throw new AssignmentException(e);
            }
        }
        return filePath;
    }

    /**
     * 删除指定路径的文件
     * @param paths 要删除的文件路径数组
     */
    private void _removeFile(String[] paths) {
        for (String path : paths) {
            File file = new File(rootDir + File.separator + path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 生成唯一ID
     * @return 生成的ID
     */
    private long _generateId() {
        return System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000);
    }

    /**
     * 将知识库插入到Elasticsearch索引
     * @param knowledgePoints 知识库实体
     * @param user 用户信息
     * @throws IOException 索引操作异常
     */
    private void _insertIntoIndex(KnowledgePoints knowledgePoints,UserVO user) throws IOException {
        Map<String, Object> source = new HashMap<>();
        source.put("kpId", knowledgePoints.getKpId());
        source.put("title",knowledgePoints.getTitle());
        source.put("content", knowledgePoints.getContent());
        source.put("tags", knowledgePoints.getTags());
        source.put("authorId", knowledgePoints.getAuthorId());
        source.put("authorName", user.getName());
        source.put("createdAt", knowledgePoints.getCreatedAt());
        source.put("updatedAt", knowledgePoints.getUpdatedAt());
        source.put("status", knowledgePoints.getStatus());

        // 创建IndexRequest对象
        IndexRequest request = new IndexRequest(indexName)
                .source(source, XContentType.JSON);

        // 执行插入操作
        client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 更新Elasticsearch索引中的知识库
     * @param knowledgePoints 知识库实体
     * @param user 用户信息
     * @throws IOException 索引操作异常
     */
    private void _updateIntoIndex(KnowledgePoints knowledgePoints,UserVO user) throws IOException {
        // 查询原索引id
        String id = _getIndexId(knowledgePoints.getKpId());
        // 更新
        Map<String, Object> source = new HashMap<>();
        source.put("title",knowledgePoints.getTitle());
        source.put("content", knowledgePoints.getContent());
        source.put("tags", knowledgePoints.getTags());
        source.put("authorName", user.getName());
        source.put("updatedAt", knowledgePoints.getUpdatedAt());
        source.put("status", knowledgePoints.getStatus());
        UpdateRequest request = new UpdateRequest(indexName, id); // 索引名和文档ID
        request.doc(source);
        client.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 获取知识库在Elasticsearch中的索引ID
     * @param kpId 知识库ID
     * @return 索引ID
     * @throws IOException 索引查询异常
     */
    private String _getIndexId(Long kpId) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder query = QueryBuilders.matchQuery("kpId", kpId);
        searchSourceBuilder.query(query);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        if (hits.getTotalHits() ==null) {
            throw new RuntimeException("该知识库不存在,无法更新");
        }
        return hits.getHits()[0].getId();
    }

    /**
     * 从Elasticsearch索引中删除知识库
     * @param kpId 知识库ID
     * @throws IOException 索引操作异常
     */
    private void _deleteFromIndex(Long kpId) throws IOException {
        String id = _getIndexId(kpId);
        DeleteRequest request = new DeleteRequest(indexName, id);
        client.delete(request, RequestOptions.DEFAULT);
    }

    /**
     * 更新知识库在Elasticsearch中的状态
     * @param kpId 知识库ID
     * @param status 新状态
     * @throws IOException 索引操作异常
     */
    private void _updateStatus(Long kpId, String status) throws IOException {
        String id = _getIndexId(kpId);
        Map<String, Object> source = new HashMap<>();
        source.put("status", status);
        UpdateRequest request = new UpdateRequest(indexName, id); // 索引名和文档ID
        request.doc(source);
        client.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 从Elasticsearch搜索知识库
     * @param sort 排序方式
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param createUid 创建用户ID
     * @param userId 当前用户ID
     * @return 知识库列表
     */
    private List<KnowledgeListVO> _searchFromElasticsearch(int sort, int page, int size, String keyword, Long createUid, long userId, String status) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        boolQuery.should(QueryBuilders.matchQuery("title", keyword));
        boolQuery.should(QueryBuilders.matchQuery("content", keyword));
        boolQuery.should(QueryBuilders.matchQuery("tags", keyword));
        boolQuery.should(QueryBuilders.matchQuery("authorName", keyword));
        
        if (createUid != null) {
            boolQuery.must(QueryBuilders.matchQuery("authorId", createUid));
        }
        if (!(userId != 0 && createUid != null && createUid == userId)) {
            boolQuery.must(QueryBuilders.matchQuery("status", KnowledgePointStatus.PUBLISHED.getValue()));
        } else if (status != null && !status.isEmpty()) {
            // 如果是查看自己的知识库，且传入了status参数，则按status过滤
            boolQuery.must(QueryBuilders.matchQuery("status", status));
        }
        
        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.from((page - 1) * size);
        searchSourceBuilder.size(size);
        
        if (sort == 1) {
            searchSourceBuilder.sort(new FieldSortBuilder("createdAt").order(SortOrder.DESC));
        } else if (sort == 2) {
            searchSourceBuilder.sort(new FieldSortBuilder("updatedAt").order(SortOrder.DESC));
        }
        
        searchRequest.source(searchSourceBuilder);
        
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            if (hits.getTotalHits() != null) {
                log.info("总匹配数: {}", hits.getTotalHits().value);
            }
            
            List<KnowledgeListVO> vos = new ArrayList<>();
            for (SearchHit hit : hits) {
                Map<String, Object> source = hit.getSourceAsMap();
                KnowledgeListVO vo = new KnowledgeListVO();
                // 安全处理各个字段，避免null值导致的异常
                Object kpIdObj = source.get("kpId");
                vo.setKpId(kpIdObj != null ? ((Number) kpIdObj).longValue() : null);
                vo.setTitle((String) source.get("title"));
                vo.setTags((String) source.get("tags"));
                // 安全处理时间字段，避免null值导致的异常
                String createdAtStr = (String) source.get("createdAt");
                String updatedAtStr = (String) source.get("updatedAt");
                
                if (createdAtStr != null && !createdAtStr.isEmpty()) {
                    vo.setCreatedAt(OffsetDateTime.parse(createdAtStr).toLocalDateTime());
                } else {
                    vo.setCreatedAt(LocalDateTime.now()); // 设置默认值
                }
                
                if (updatedAtStr != null && !updatedAtStr.isEmpty()) {
                    vo.setUpdatedAt(OffsetDateTime.parse(updatedAtStr).toLocalDateTime());
                } else {
                    vo.setUpdatedAt(LocalDateTime.now()); // 设置默认值
                }
                vo.setStatus((String) source.get("status"));
                
                KnowledgeListVO.Creator creator = new KnowledgeListVO.Creator();
                Object authorIdObj = source.get("authorId");
                Long authorId = authorIdObj != null ? ((Number) authorIdObj).longValue() : null;
                creator.setId(authorId);
                
                if (authorId != null) {
                    UserVO user = userClient.getUserInfo(authorId).getData();
                    creator.setName(user.getName());
                    creator.setAvatar(user.getAvatorUrl());
                } else {
                    creator.setName("未知用户");
                    creator.setAvatar("");
                }
                vo.setCreator(creator);
                
                String content = (String) source.get("content");
                if (content != null && !content.isEmpty()) {
                    vo.setSimpleContent(content.substring(0, Math.min(50, content.length())));
                } else {
                    vo.setSimpleContent(""); // 设置默认值
                }
                
                float searchScore = hit.getScore();
                vo.setSearchScore(searchScore);
                vo.setLikeCount(userLikeMapper.countByKpId(vo.getKpId()));
                vos.add(vo);
            }
            return vos;
        } catch (Exception e) {
            log.error("查询索引库失败:", e);
            throw new RuntimeException("查询索引库失败");
        }
    }

    /**
     * 从MySQL查询知识库列表
     * @param sort 排序方式
     * @param page 页码
     * @param size 每页大小
     * @param createUid 创建用户ID
     * @param userId 当前用户ID
     * @return 知识库列表
     */
    private List<KnowledgeListVO> _queryFromMySQL(int sort, int page, int size, Long createUid, long userId, String status) {
        // 构建查询条件
        LambdaQueryWrapper<KnowledgePoints> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加创建者过滤条件
        if (createUid != null) {
            queryWrapper.eq(KnowledgePoints::getAuthorId, createUid);
        }

        // 如果不是查看自己的知识库，只显示已发布的
        if (!(userId != 0 && createUid != null && createUid == userId)) {
            queryWrapper.eq(KnowledgePoints::getStatus, KnowledgePointStatus.PUBLISHED.getValue());
        } else if (status != null && !status.isEmpty()) {
            // 如果是查看自己的知识库，且传入了status参数，则按status过滤
            queryWrapper.eq(KnowledgePoints::getStatus, status);
        }

        // 添加排序条件
        if (sort == 1) {
            queryWrapper.orderByDesc(KnowledgePoints::getCreatedAt);
        } else if (sort == 2) {
            queryWrapper.orderByDesc(KnowledgePoints::getUpdatedAt);
        }
        
        // 分页查询
        List<KnowledgePoints> knowledgePointsList = list(queryWrapper);
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(page * size, knowledgePointsList.size());
        
        // 检查分页参数是否有效
        if (startIndex >= knowledgePointsList.size()) {
            return new ArrayList<>();
        }
        
        knowledgePointsList = knowledgePointsList.subList(startIndex, endIndex);
        List<KnowledgeListVO> vos = new ArrayList<>();
        for (KnowledgePoints knowledge : knowledgePointsList) {
            KnowledgeListVO vo = new KnowledgeListVO();
            vo.setKpId(knowledge.getKpId());
            vo.setTitle(knowledge.getTitle());
            vo.setTags(knowledge.getTags());
            vo.setCreatedAt(knowledge.getCreatedAt());
            vo.setUpdatedAt(knowledge.getUpdatedAt());
            vo.setStatus(knowledge.getStatus());
            
            // 获取创建者信息
            KnowledgeListVO.Creator creator = new KnowledgeListVO.Creator();
            Long authorId = knowledge.getAuthorId();
            creator.setId(authorId);
            
            if (authorId != null) {
                UserVO user = userClient.getUserInfo(authorId).getData();
                creator.setName(user.getName());
                creator.setAvatar(user.getAvatorUrl());
            } else {
                creator.setName("未知用户");
                creator.setAvatar("");
            }
            vo.setCreator(creator);
            
            // 设置简单内容
            String content = knowledge.getContent();
            if (content != null && !content.isEmpty()) {
                vo.setSimpleContent(content.substring(0, Math.min(50, content.length())));
            } else {
                vo.setSimpleContent(""); // 设置默认值
            }
            
            // 非搜索操作，搜索分数设为-1
            vo.setSearchScore(-1);
            vo.setLikeCount(userLikeMapper.countByKpId(vo.getKpId()));
            vos.add(vo);
        }
        
        return vos;
    }

    /**
     * 生成Redis缓存键
     * @param sort 排序方式
     * @param page 页码
     * @param size 每页大小
     * @param createUid 创建用户ID
     * @param userId 当前用户ID
     * @return 缓存键
     */
    private String _generateCacheKey(int sort, int page, int size, Long createUid, long userId,String status) {
        return String.format("knowledge:list:sort_%d:page_%d:size_%d:createUid_%s:userId_%d:status_%s",
            sort, page, size, createUid != null ? createUid.toString() : "null", userId,status!=null?status:"null");
    }

    /**
     * 清除知识库相关的Redis缓存
     */
    private void _clearKnowledgeCache() {
        try {
            Set<String> keys = redisTemplate.keys("knowledge:list:*");
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("已清除知识库列表缓存，清除数量: {}", keys.size());
            }
        } catch (Exception e) {
            log.warn("清除知识库缓存失败: {}", e.getMessage());
        }
    }

    // 点赞相关方法实现
    @Override
    public void toggleLike(Long kpId, long userId) {
        UserLike existingLike = userLikeMapper.selectByKpIdAndUserId(kpId, userId);
        if (existingLike != null) {
            // 取消点赞
            userLikeMapper.deleteById(existingLike.getId());
        } else {
            // 添加点赞
            UserLike like = new UserLike();
            like.setKpId(kpId);
            like.setUserId(userId);
            like.setCreatedAt(LocalDateTime.now());
            userLikeMapper.insert(like);
        }
    }

    // 收藏相关方法实现
    @Override
    public void toggleFavorite(Long kpId, long userId) {
        UserFavorite existingFavorite = userFavoriteMapper.selectByKpIdAndUserId(kpId, userId);
        if (existingFavorite != null) {
            // 取消收藏
            userFavoriteMapper.deleteById(existingFavorite.getId());
        } else {
            // 添加收藏
            UserFavorite favorite = new UserFavorite();
            favorite.setKpId(kpId);
            favorite.setUserId(userId);
            favorite.setCreatedAt(LocalDateTime.now());
            userFavoriteMapper.insert(favorite);
        }
    }

    // 评论相关方法实现
    @Override
    public Map<String, Object> getCommentsWithPagination(Long kpId, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 获取总评论数
        int total = commentMapper.countByKpId(kpId);
        
        // 获取主评论
        List<Comment> comments = commentMapper.selectList(
            new LambdaQueryWrapper<Comment>()
                .eq(Comment::getKpId, kpId)
                .isNull(Comment::getParentId)
                .orderByDesc(Comment::getCreatedAt)
                .last("LIMIT " + size + " OFFSET " + offset)
        );
        
        List<CommentVO> commentVOs = new ArrayList<>();
        for (Comment comment : comments) {
            CommentVO vo = new CommentVO();
            vo.setId(comment.getId());
            vo.setKpId(comment.getKpId());
            vo.setContent(comment.getContent());
            vo.setParentId(comment.getParentId());
            vo.setCreatedAt(comment.getCreatedAt());
            vo.setUpdatedAt(comment.getUpdatedAt());
            
            // 获取评论者信息
            UserVO user = userClient.getUserInfo(comment.getUserId()).getData();
            KnowledgeListVO.Creator creator = new KnowledgeListVO.Creator();
            creator.setId(user.getUid());
            creator.setName(user.getName());
            creator.setAvatar(user.getAvatorUrl());
            vo.setUser(creator);
            
            // 获取子评论
            List<Comment> replies = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getParentId, comment.getId())
                    .orderByAsc(Comment::getCreatedAt)
            );
            
            List<CommentVO> replyVOs = new ArrayList<>();
            for (Comment reply : replies) {
                CommentVO replyVO = new CommentVO();
                replyVO.setId(reply.getId());
                replyVO.setKpId(reply.getKpId());
                replyVO.setContent(reply.getContent());
                replyVO.setParentId(reply.getParentId());
                replyVO.setCreatedAt(reply.getCreatedAt());
                replyVO.setUpdatedAt(reply.getUpdatedAt());
                
                UserVO replyUser = userClient.getUserInfo(reply.getUserId()).getData();
                KnowledgeListVO.Creator replyCreator = new KnowledgeListVO.Creator();
                replyCreator.setId(replyUser.getUid());
                replyCreator.setName(replyUser.getName());
                replyCreator.setAvatar(replyUser.getAvatorUrl());
                replyVO.setUser(replyCreator);
                
                replyVOs.add(replyVO);
            }
            vo.setReplies(replyVOs);
            
            commentVOs.add(vo);
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("comments", commentVOs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("hasMore", (page * size) < total);
        
        return result;
    }

    @Override
    public void addComment(Long kpId, long userId, String content, Long parentId) {
        Comment comment = new Comment();
        comment.setKpId(kpId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        commentMapper.insert(comment);
    }

    @Override
    public void deleteComment(Long commentId, long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 检查权限：只有评论作者才能删除
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此评论");
        }
        
        // 删除子评论
        commentMapper.delete(
            new LambdaQueryWrapper<Comment>()
                .eq(Comment::getParentId, commentId)
        );
        
        // 删除主评论
        commentMapper.deleteById(commentId);
    }

    // 历史记录相关方法实现
    @Override
    public void addHistory(Long kpId, long userId, Integer duration) {
        // 检查是否已存在历史记录
        UserHistory existingHistory = userHistoryMapper.selectByUserIdAndKpId(userId, kpId);
        
        if (existingHistory != null) {
            // 更新现有记录
            existingHistory.setViewedAt(LocalDateTime.now());
            if (duration != null) {
                existingHistory.setDuration(duration);
            }
            userHistoryMapper.updateById(existingHistory);
        } else {
            // 创建新记录
            UserHistory history = new UserHistory();
            history.setKpId(kpId);
            history.setUserId(userId);
            history.setViewedAt(LocalDateTime.now());
            history.setDuration(duration != null ? duration : 0);
            history.setCreatedAt(LocalDateTime.now());
            userHistoryMapper.insert(history);
        }
    }

    @Override
    public List<HistoryVO> getHistoryList(long userId, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 获取历史记录
        List<UserHistory> histories = userHistoryMapper.selectList(
            new LambdaQueryWrapper<UserHistory>()
                .eq(UserHistory::getUserId, userId)
                .orderByDesc(UserHistory::getViewedAt)
                .last("LIMIT " + size + " OFFSET " + offset)
        );
        
        List<HistoryVO> historyVOs = new ArrayList<>();
        for (UserHistory history : histories) {
            // 获取知识点信息
            KnowledgePoints knowledge = getById(history.getKpId());
            if (knowledge != null) {
                HistoryVO vo = new HistoryVO();
                vo.setId(history.getId());
                vo.setKpId(history.getKpId());
                vo.setTitle(knowledge.getTitle());
                vo.setTags(knowledge.getTags());
                vo.setViewedAt(history.getViewedAt());
                vo.setDuration(history.getDuration());
                vo.setCreatedAt(history.getCreatedAt());
                
                // 设置简单内容
                String content = knowledge.getContent();
                if (content != null && !content.isEmpty()) {
                    vo.setSimpleContent(content.substring(0, Math.min(100, content.length())));
                } else {
                    vo.setSimpleContent("");
                }
                
                // 获取创建者信息
                KnowledgeListVO.Creator creator = new KnowledgeListVO.Creator();
                Long authorId = knowledge.getAuthorId();
                creator.setId(authorId);
                
                if (authorId != null) {
                    UserVO user = userClient.getUserInfo(authorId).getData();
                    creator.setName(user.getName());
                    creator.setAvatar(user.getAvatorUrl());
                } else {
                    creator.setName("未知用户");
                    creator.setAvatar("");
                }
                vo.setCreator(creator);
                
                historyVOs.add(vo);
            }
        }
        
        return historyVOs;
    }

    @Override
    public void clearHistory(long userId) {
        userHistoryMapper.delete(
            new LambdaQueryWrapper<UserHistory>()
                .eq(UserHistory::getUserId, userId)
        );
    }

    // 收藏相关方法实现
    @Override
    public List<FavoriteVO> getFavoriteList(long userId, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 获取收藏记录
        List<UserFavorite> favorites = userFavoriteMapper.selectList(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .orderByDesc(UserFavorite::getCreatedAt)
                .last("LIMIT " + size + " OFFSET " + offset)
        );
        
        List<FavoriteVO> favoriteVOs = new ArrayList<>();
        for (UserFavorite favorite : favorites) {
            // 获取知识点信息
            KnowledgePoints knowledge = getById(favorite.getKpId());
            if (knowledge != null) {
                FavoriteVO vo = new FavoriteVO();
                vo.setId(favorite.getId());
                vo.setKpId(favorite.getKpId());
                vo.setTitle(knowledge.getTitle());
                vo.setTags(knowledge.getTags());
                vo.setFavoritedAt(favorite.getCreatedAt());
                vo.setCreatedAt(favorite.getCreatedAt());
                
                // 设置简单内容
                String content = knowledge.getContent();
                if (content != null && !content.isEmpty()) {
                    vo.setSimpleContent(content.substring(0, Math.min(100, content.length())));
                } else {
                    vo.setSimpleContent("");
                }
                
                // 获取创建者信息
                KnowledgeListVO.Creator creator = new KnowledgeListVO.Creator();
                Long authorId = knowledge.getAuthorId();
                creator.setId(authorId);
                
                if (authorId != null) {
                    UserVO user = userClient.getUserInfo(authorId).getData();
                    creator.setName(user.getName());
                    creator.setAvatar(user.getAvatorUrl());
                } else {
                    creator.setName("未知用户");
                    creator.setAvatar("");
                }
                vo.setCreator(creator);
                
                favoriteVOs.add(vo);
            }
        }
        
        return favoriteVOs;
    }

    @Override
    public List<KnowledgeAnalysis> getAnalysis(long userId) {
        LambdaQueryWrapper<KnowledgePoints> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KnowledgePoints::getStatus, KnowledgePointStatus.PUBLISHED.getValue());
        queryWrapper.eq(KnowledgePoints::getAuthorId,userId);
        List<KnowledgePoints> knowledgePoints = list(queryWrapper);

        List<KnowledgeAnalysis> analysisList = new ArrayList<>();
        knowledgePoints.forEach(kp->{
            KnowledgeAnalysis analysis = new KnowledgeAnalysis();
            analysis.setId(kp.getKpId());
            analysis.setTitle(kp.getTitle());
            // 获取知识点点赞数
            analysis.setLikeCount(userLikeMapper.countByKpId(kp.getKpId()));
            // 获取知识点收藏数
            analysis.setFavoriteCount(userFavoriteMapper.countByKpId(kp.getKpId()));
            // 获取知识点评论数
            analysis.setCommentCount(commentMapper.countByKpId(kp.getKpId()));
            // 获取知识点浏览数
            analysis.setViewCount(userHistoryMapper.countByKpId(kp.getKpId()));
            analysisList.add(analysis);
        });
        analysisList.sort(Comparator.comparing(KnowledgeAnalysis::getViewCount, Comparator.reverseOrder()));
        return analysisList.subList(0, Math.min(analysisList.size(), 5));
    }
}
