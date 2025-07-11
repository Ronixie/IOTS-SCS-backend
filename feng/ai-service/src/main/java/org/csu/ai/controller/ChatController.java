package org.csu.ai.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.ai.entity.ChatDTO;
import org.csu.ai.entity.po.ChatMemory;
import org.csu.ai.entity.vo.ChatHistoryVO;
import org.csu.ai.service.ChatService;
import org.csu.ai.service.FileUploadService;
import org.csu.exception.AIException;
import org.csu.exception.UnauthorizedException;
import org.csu.utils.Result;
import org.csu.utils.UserContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI聊天相关接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
@Slf4j
public class ChatController {
    private final FileUploadService fileUploadService;
    private final ChatService chatService;

    /**
     * AI聊天
     *
     * @param chatId 会话Id
     * @return 流式信息
     */
    @Operation(summary = "AI聊天")
    @Parameters({
            @Parameter(name = "chatId", description = "会话Id", required = true),
            @Parameter(name = "prompt", description = "提示词", required = true),
            @Parameter(name = "files", description = "文档/图片")
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping("/chat/res/{chatId}")
    public Flux<String> chat(@PathVariable("chatId") String chatId, @RequestBody ChatDTO chatDTO) {
        String prompt = chatDTO.getPrompt();
        List<String> files = chatDTO.getFiles();
        log.info("AI聊天请求 - chatId: {}, prompt: {}, files: {}", chatId, prompt, files);
        if (prompt == null || prompt.isEmpty()) {
            log.error("提示词为空");
            throw new AIException("提示词不能为空");
        }
        // 获取用户id
        long userId = UserContext.getUser();
        if (!chatService.isUserConnectChat(chatId, userId)) {
            log.error("AIChat:非法AI会话 - chatId: {}, userId: {}", chatId, userId);
            throw new AIException("非法的会话");
        }
        if (files == null || files.isEmpty()) {
            return chatService.chat(chatId, prompt);
        } else {
            return chatService.chat(chatId, prompt, files);
        }
    }

    @Operation(summary = "获取会话信息")
    @Parameter(name = "chatId", description = "会话Id", required = true)
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatMemory.class)))
    @GetMapping("/chat/{chatId}")
    public Result<List<ChatMemory>> getChatMemory(@PathVariable("chatId") String chatId) {
        log.info("获取会话列表请求 - chatId: {}", chatId);
        // 获取用户id
        long userId = UserContext.getUser();
        if (!chatService.isUserConnectChat(chatId, userId)) {
            log.error("获取会话列表：非法AI会话 - chatId: {}, userId: {}", chatId, userId);
            throw new AIException("非法的会话");
        }
        return Result.success(chatService.getChatMemory(chatId));
    }

    /**
     * 上传ai聊天文件
     *
     * @param file 源文件
     * @return 带有路径的Result
     */
    @Operation(summary = "上传文件")
    @Parameters({
            @Parameter(name = "file", description = "文件", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class)))
    @PostMapping("/upload/{chatId}")
    public Result<String> uploadFile(@PathVariable("chatId") String chatId,
                                     @RequestParam("file") MultipartFile file) {
        log.info("文件上传请求 - chatId: {}, fileName: {}", chatId, file.getOriginalFilename());
        // 获取用户id
        long userId = UserContext.getUser();
        if (!chatService.isUserConnectChat(chatId, userId)) {
            log.error("上传文件：非法AI会话 - chatId: {}, userId: {}", chatId, userId);
            return Result.error("非法的会话");
        }
        String url;
        try {
            url = fileUploadService.uploadFile(file, userId, chatId);
        } catch (Exception e) {
            log.error("文件上传失败 - chatId: {}, error: {}", chatId, e.getMessage());
            return Result.error("上传失败");
        }
        if (url != null) {
            log.info("文件上传成功 - chatId: {}, url: {}", chatId, url);
            return Result.success(url);
        } else {
            log.error("文件上传失败 - chatId: {}", chatId);
            return Result.error("上传失败");
        }
    }

    @Operation(summary = "获取会话列表")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatHistoryVO.class)))
    @GetMapping("/chat")
    public Result<List<ChatHistoryVO>> getChatHistory() {
        // 获取用户id
        long userId = UserContext.getUser();
        if (userId == 0) {
            log.error("获取会话列表失败 - userId为空");
            throw new UnauthorizedException("userId为空");
        }
        log.info("获取会话列表请求 - userId: {}", userId);
        return Result.success(chatService.getChatHistory(userId));
    }

    /**
     * 创建新的会话
     *
     * @return 会话id
     */
    @Operation(summary = "创建新的会话")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class)))
    @PostMapping("/chat")
    public Result<String> createNewChat() {
        // 获取用户id
        long userId = UserContext.getUser();
        if (userId == 0) {
            log.error("创建会话失败 - userId为空");
            throw new UnauthorizedException("userId为空");
        }
        String chatId = chatService.createNewChat(userId);
        if (chatId != null) {
            log.info("创建会话成功 - userId: {}, chatId: {}", userId, chatId);
            return Result.success(chatId);
        } else {
            log.error("创建会话失败 - userId: {}", userId);
            return Result.error("创建失败");
        }
    }

    /**
     * 删除会话
     *
     * @param chatId 会话id
     * @return 删除结果
     */
    @Operation(summary = "删除会话")
    @Parameter(name = "chatId", description = "会话id", required = true)
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class)))
    @DeleteMapping("/chat/{chatId}")
    public Result<String> deleteChat(@PathVariable("chatId") String chatId) {
        // 获取用户id
        long userId = UserContext.getUser();
        if (userId == 0) {
            log.error("删除会话失败 - userId为空");
            throw new UnauthorizedException("userId为空");
        }
        if(chatService.deleteChat(chatId, userId)){
            log.info("删除会话成功 - userId: {}, chatId: {}", userId, chatId);
            return Result.success("删除成功");
        }else{
            log.error("删除会话失败 - userId: {}, chatId: {}", userId, chatId);
            return Result.error("删除失败");
        }
    }
    /**
     * AI评分
     *
     * @param prompt 试卷信息+学生答题信息
     * @return 评分结果
     */
    @Operation(summary = "AI评分")
    @Parameters({
            @Parameter(name = "prompt", description = "试卷信息+学生答题信息", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("/score")
    public String aiToScore(@RequestParam("prompt") String prompt) {
        log.info("AI评分请求 - prompt: {}", prompt);
        return chatService.aiToScore(prompt);
    }

    /**
     * 题目解析
     */
    @Operation(summary = "题目解析")
    @Parameters({
            @Parameter(name = "paperId", description = "试卷ID", required = true),
            @Parameter(name = "questionId", description = "题目ID", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("/analysis/exam/{paperId}/{questionId}")
    public Flux<String> analysis(@PathVariable("paperId") String paperId, @PathVariable("questionId") String questionId) {
        log.info("题目解析请求 - 试卷：{},题目：{},用户：{}", paperId, questionId, UserContext.getUser());
        return chatService.analysisExamPaperQuestion(paperId, questionId);
    }

    @Operation(summary = "总结知识库内容")
    @Parameters({
            @Parameter(name = "kpId", description = "知识库Id", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("/knowledge/summary")
    public Flux<String> summary(@RequestParam("kpId") Long kpId)  {
        log.info("总结知识库内容请求 - kpId: {}", kpId);
        return chatService.summaryKnowledge(kpId);
    }


    @Operation(summary = "内部调用接口，进行学生学习分析")
    @Parameters({
            @Parameter(name = "prompt", description = "学生学习情况", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("/analysis/student")
    public String analysisStudent(@RequestParam("prompt") String prompt) {
        log.info("AI建议请求 - prompt: {}", prompt);
        return chatService.analysisStudent(prompt);
    }

    @GetMapping("/generate")
    public String generate(@RequestParam("prompt") String prompt) {
        return chatService.generate(prompt);
    }
}