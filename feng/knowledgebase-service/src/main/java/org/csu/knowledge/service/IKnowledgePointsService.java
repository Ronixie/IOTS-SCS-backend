package org.csu.knowledge.service;

import org.csu.knowledge.entity.dto.KnowledgeDTO;
import org.csu.knowledge.entity.po.KnowledgePoints;
import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.knowledge.entity.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2025-07-05
 */
public interface IKnowledgePointsService extends IService<KnowledgePoints> {

    List<KnowledgeListVO> browseKnowledge(int sort, int page, int size, String keyword, Long createUid,long userId,String status);

    KnowledgeDetailVO getKnowledge(Long kpId, long userId);

    Long addOrUpdateKnowledge(KnowledgeDTO dto, MultipartFile[] files, long userId, boolean isDraft);

    void deleteKnowledge(Long kpId, long userId);

    void updateStatus(Long id, String status, long userId);

    File download(Long kpId, long userId, String attachment);

    // 点赞相关方法
    void toggleLike(Long kpId, long userId);

    // 收藏相关方法
    void toggleFavorite(Long kpId, long userId);

    // 评论相关方法
    Map<String, Object> getCommentsWithPagination(Long kpId, int page, int size);
    void addComment(Long kpId, long userId, String content, Long parentId);
    void deleteComment(Long commentId, long userId);

    // 历史记录相关方法
    void addHistory(Long kpId, long userId, Integer duration);
    List<HistoryVO> getHistoryList(long userId, int page, int size);
    void clearHistory(long userId);

    // 收藏相关方法
    List<FavoriteVO> getFavoriteList(long userId, int page, int size);

    List<KnowledgeAnalysis> getAnalysis(long userId);
}
