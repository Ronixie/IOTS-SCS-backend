package org.csu.knowledge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.exception.UnauthorizedException;
import org.csu.knowledge.entity.dto.KnowledgeDTO;
import org.csu.knowledge.entity.po.KnowledgePoints;
import org.csu.knowledge.entity.vo.*;
import org.csu.knowledge.service.IKnowledgePointsService;
import org.csu.utils.Result;
import org.csu.utils.UserContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
@Slf4j
public class KnowledgeController {
    private final IKnowledgePointsService knowledgeService;

    /**
     * 浏览知识库
     *
     * @param sort 排序方式 0-默认 ,1-创建时间，2-更新时间
     * @return
     */
    @Operation(summary = "浏览知识库")
    @Parameters({
            @Parameter(name = "sort", description = "排序方式 0-默认 ,1-创建时间，2-更新时间"),
            @Parameter(name = "page", description = "页码"),
            @Parameter(name = "size", description = "每页数量", required = true),
            @Parameter(name = "keyword", description = "关键字"),
            @Parameter(name = "createUid", description = "创建者id")
    })
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = KnowledgeListVO.class))
    )
    @GetMapping
    public Result<List<KnowledgeListVO>> browseKnowledge(@RequestParam(value = "sort", defaultValue = "0", required = false) int sort, @RequestParam(value = "page") int page,
                                                         @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                         @RequestParam(value = "keyword", required = false) String keyword,
                                                         @RequestParam(value = "createUid", required = false) Long createUid,
                                                         @RequestParam(value = "status", required = false) String status) {
        log.info("浏览知识库");
        long userId = UserContext.getUser();
        List<KnowledgeListVO> knowledgeList = knowledgeService.browseKnowledge(sort, page, size, keyword, createUid, userId,status);
        return Result.success(knowledgeList);
    }

    @Operation(summary = "获取指定知识库")
    @Parameters({
            @Parameter(name = "kpId", description = "知识库ID")
    })
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = KnowledgePoints.class))
    )
    @GetMapping("/{kpId}")
    public Result<KnowledgeDetailVO> getKnowledge(@PathVariable("kpId") Long kpId) {
        log.info("获取指定知识库：{}", kpId);
        long userId = UserContext.getUser();
        KnowledgeDetailVO vo = knowledgeService.getKnowledge(kpId, userId);
        return Result.success(vo);
    }

    @Operation(summary = "保存知识库草稿")
    @Parameters({
            @Parameter(name = "dto", description = "知识库数据传输对象"),
            @Parameter(name = "files", description = "上传文件")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping("/draft")
    public Result<String> addDraftKnowledge(@RequestPart("dto") KnowledgeDTO dto, @RequestPart(value = "files",required = false) MultipartFile[] files) {
        log.info("保存知识库草稿");
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        Long kpId=knowledgeService.addOrUpdateKnowledge(dto, files, userId, true);
        return Result.success(kpId.toString());
    }

    @Operation(summary = "更新知识库草稿")
    @Parameters({
            @Parameter(name = "dto", description = "知识库数据传输对象"),
            @Parameter(name = "files", description = "上传文件")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PutMapping("/draft")
    public Result<String> updateDraftKnowledge(@RequestPart("dto") KnowledgeDTO dto, @RequestPart(value = "files",required = false) MultipartFile[] files) {
        log.info("更新知识库草稿");
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        Long kpId=knowledgeService.addOrUpdateKnowledge(dto, files, userId, true);
        return Result.success(kpId.toString());
    }

    @Operation(summary = "发布知识库")
    @Parameters({
            @Parameter(name = "dto", description = "知识库数据传输对象"),
            @Parameter(name = "files", description = "上传文件")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping
    public Result<String> publishKnowledge(@RequestPart("dto") KnowledgeDTO dto, @RequestPart(value = "files",required = false) MultipartFile[] files) {
        log.info("发布知识库");
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        Long kpId=knowledgeService.addOrUpdateKnowledge(dto, files, userId, false);
        return Result.success(kpId.toString());
    }

    @Operation(summary = "更新知识库")
    @Parameters({
            @Parameter(name = "dto", description = "知识库数据传输对象"),
            @Parameter(name = "files", description = "上传文件")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PutMapping
    public Result<String> updateKnowledge(@RequestPart("dto") KnowledgeDTO dto, @RequestPart(value = "files",required = false) MultipartFile[] files) {
        log.info("更新知识库");
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        Long kpId=knowledgeService.addOrUpdateKnowledge(dto, files, userId, false);
        return Result.success(kpId.toString());
    }

    @Operation(summary = "删除知识库")
    @Parameters({
            @Parameter(name = "kpId", description = "知识库ID")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @DeleteMapping("/{kpId}")
    public Result<String> deleteKnowledge(@PathVariable("kpId") Long kpId) {
        log.info("删除知识库：{}", kpId);
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        knowledgeService.deleteKnowledge(kpId, userId);
        return Result.success();
    }

    @Operation(summary = "更新知识库状态")
    @Parameters({
            @Parameter(name = "id", description = "知识库ID"),
            @Parameter(name = "status", description = "状态")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PutMapping("/status/{id}")
    public Result<String> updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        log.info("更新知识库状态：{}", id);
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        knowledgeService.updateStatus(id, status, userId);
        return Result.success();
    }

    @Operation(summary = "下载知识库附件")
    @Parameters({
            @Parameter(name = "kpId", description = "知识库ID"),
            @Parameter(name = "attachment", description = "附件名称")
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(mediaType = "application/octet-stream",schema = @Schema(implementation = InputStreamResource.class)))
    @GetMapping("/download/{kpId}")
    public ResponseEntity<InputStreamResource> download(@PathVariable("kpId") Long kpId, @RequestParam("attachment") String attachment) {
        log.info("下载知识库附件：{},{}", kpId, attachment);
        long userId = UserContext.getUser();
        File file = knowledgeService.download(kpId, userId, attachment);
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            log.error("下载附件失败", e);
            throw new RuntimeException("下载附件失败");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8)
                .replace("+", "%20"));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }

    @Operation(summary = "点赞知识点")
    @Parameters({
            @Parameter(name = "kpId", description = "知识点ID")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping("/{kpId}/like")
    public Result<String> likeKnowledge(@PathVariable("kpId") Long kpId) {
        log.info("点赞知识点：{}", kpId);
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        knowledgeService.toggleLike(kpId, userId);
        return Result.success();
    }

    @Operation(summary = "收藏知识点")
    @Parameters({
            @Parameter(name = "kpId", description = "知识点ID")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping("/{kpId}/favorite")
    public Result<String> favoriteKnowledge(@PathVariable("kpId") Long kpId) {
        log.info("收藏知识点：{}", kpId);
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        knowledgeService.toggleFavorite(kpId, userId);
        return Result.success();
    }

    @Operation(summary = "获取知识点评论")
    @Parameters({
            @Parameter(name = "kpId", description = "知识点ID"),
            @Parameter(name = "page", description = "页码"),
            @Parameter(name = "size", description = "每页数量")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("/{kpId}/comments")
    public Result<Map<String, Object>> getComments(@PathVariable("kpId") Long kpId,
                                              @RequestParam(value = "page", defaultValue = "1") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取知识点评论：{}", kpId);
        Map<String, Object> result = knowledgeService.getCommentsWithPagination(kpId, page, size);
        return Result.success(result);
    }

    @Operation(summary = "添加评论")
    @Parameters({
            @Parameter(name = "kpId", description = "知识点ID"),
            @Parameter(name = "content", description = "评论内容"),
            @Parameter(name = "parentId", description = "父评论ID（可选）")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping("/{kpId}/comments")
    public Result<String> addComment(@PathVariable("kpId") Long kpId,
                                    @RequestParam("content") String content,
                                    @RequestParam(value = "parentId", required = false) Long parentId) {
        log.info("添加评论：{}", kpId);
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        knowledgeService.addComment(kpId, userId, content, parentId);
        return Result.success();
    }

    @Operation(summary = "删除评论")
    @Parameters({
            @Parameter(name = "commentId", description = "评论ID")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @DeleteMapping("/comments/{commentId}")
    public Result<String> deleteComment(@PathVariable("commentId") Long commentId) {
        log.info("删除评论：{}", commentId);
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        knowledgeService.deleteComment(commentId, userId);
        return Result.success();
    }

    @Operation(summary = "添加浏览历史")
    @Parameters({
            @Parameter(name = "kpId", description = "知识点ID"),
            @Parameter(name = "duration", description = "停留时长（秒）")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping("/{kpId}/history")
    public Result<String> addHistory(@PathVariable("kpId") Long kpId,
                                    @RequestParam(value = "duration", required = false) Integer duration) {
        log.info("添加浏览历史：{}", kpId);
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        knowledgeService.addHistory(kpId, userId, duration);
        return Result.success();
    }

    @Operation(summary = "获取浏览历史")
    @Parameters({
            @Parameter(name = "page", description = "页码"),
            @Parameter(name = "size", description = "每页数量")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("/history")
    public Result<List<HistoryVO>> getHistory(@RequestParam(value = "page", defaultValue = "1") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取浏览历史");
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        List<HistoryVO> historyList = knowledgeService.getHistoryList(userId, page, size);
        return Result.success(historyList);
    }

    @Operation(summary = "清空浏览历史")
    @ApiResponse(responseCode = "200", description = "成功")
    @DeleteMapping("/history")
    public Result<String> clearHistory() {
        log.info("清空浏览历史");
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        knowledgeService.clearHistory(userId);
        return Result.success();
    }

    @Operation(summary = "获取收藏列表")
    @Parameters({
            @Parameter(name = "page", description = "页码"),
            @Parameter(name = "size", description = "每页数量")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("/favorites")
    public Result<List<FavoriteVO>> getFavorites(@RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取收藏列表");
        long userId = UserContext.getUser();
        if (userId == 0) {
            throw new UnauthorizedException("用户未登录");
        }
        List<FavoriteVO> favoriteList = knowledgeService.getFavoriteList(userId, page, size);
        return Result.success(favoriteList);
    }

    @Operation(summary = "内部调用接口，获取信息用于分析")
    @GetMapping("/analysis")
    public Result<List<KnowledgeAnalysis>> getAnalysis() {
        log.info("获取信息用于分析");
        long userId = UserContext.getUser();
        List<KnowledgeAnalysis> analysisList = knowledgeService.getAnalysis(userId);
        return Result.success(analysisList);
    }
}
