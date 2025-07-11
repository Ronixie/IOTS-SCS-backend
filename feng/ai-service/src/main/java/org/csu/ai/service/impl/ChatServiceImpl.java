package org.csu.ai.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.ai.clients.ExamClient;
import org.csu.ai.clients.KnowledgeClient;
import org.csu.ai.entity.po.ChatMemory;
import org.csu.ai.entity.po.UserChat;
import org.csu.ai.entity.vo.ChatHistoryVO;
import org.csu.ai.memory.MongoDBChatMemory;
import org.csu.ai.repository.ChatMemoryRepository;
import org.csu.ai.service.ChatService;
import org.csu.ai.service.DocumentService;
import org.csu.ai.service.IUserChatService;
import org.csu.exception.AIException;
import org.csu.utils.Result;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 聊天服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatClient chatClient;
    private final DocumentService documentService;
    private final IUserChatService userChatService;
    private final ExamClient examClient;
    private final ChatMemoryRepository chatMemoryRepository;
    private final MongoDBChatMemory mongoDBChatMemory;
    private final KnowledgeClient knowledgeClient;
/*    private final ChatModel chatModel;
    private final FunctionCallingTools tools;*/
    /**
     * 处理聊天请求
     *
     * @param chatId 会话ID
     * @param prompt 用户输入
     * @return 返回聊天响应流
     */
    @Override
    public Flux<String> chat(String chatId, String prompt) {
        try {
            log.info("开始处理聊天请求, chatId: {}, prompt: {}", chatId, prompt);
            return chatClient.prompt(getPromptWithRAG(prompt, chatId)).advisors(
                    spec -> spec.param(org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID, chatId)
            ).stream().content();
        } catch (IOException e) {
            log.error("聊天请求处理异常, chatId: {}, prompt: {}", chatId, prompt, e);
            throw new AIException("回复异常", e);
        }
    }

    /**
     * 处理带附件的聊天请求
     *
     * @param chatId 会话ID
     * @param prompt 用户输入
     * @param files  附件路径列表
     * @return 返回聊天响应流
     */
    @Override
    public Flux<String> chat(String chatId, String prompt, List<String> files) {
        log.info("开始处理带附件的聊天请求, chatId: {}, prompt: {}, files: {}", chatId, prompt, files);
        List<Media> mediaList = new ArrayList<>();
        // 处理文件列表
        for (String path : files) {
            if (path == null || path.isEmpty()) {
                continue;
            }
            // 创建Media对象，使用Base64编码的图片数据
            mediaList.add(new Media(MimeTypeUtils.IMAGE_PNG, new FileSystemResource(path)));
        }
        try {
            // 构建带有RAG的提示词
            String fullPrompt = getPromptWithRAG(prompt, chatId);

            UserMessage userMessage = UserMessage.builder()
                    .text(fullPrompt).media(mediaList).build();
            // 发送请求到大模型
            Flux<String> response = chatClient.prompt(new Prompt(userMessage)).advisors(
                    MessageChatMemoryAdvisor.builder(mongoDBChatMemory).conversationId(chatId).build()
            ).stream().content();

            log.info("带附件的聊天请求处理完成, chatId: {}, 成功处理图片数量: {}", chatId, mediaList.size());
            return response;
        } catch (IOException e) {
            log.error("带附件的聊天请求处理异常, chatId: {}, prompt: {}, files: {}", chatId, prompt, files, e);
            throw new AIException("回复异常", e);
        }
    }

    /**
     * 构建RAG提示
     *
     * @param prompt 原始提示
     * @param chatId 会话ID
     * @return 返回构建后的提示
     * @throws IOException 文件操作异常
     */
    private String getPromptWithRAG(String prompt, String chatId) throws IOException {
        log.info("开始构建RAG提示, chatId: {}, prompt: {}", chatId, prompt);
        List<String> docs = documentService.searchSimilarDocs(prompt, chatId);
        if (docs.isEmpty()) {
            log.info("未找到相关文档, 返回原始提示");
            return prompt;
        } else {
            String ragPrompt = "请根据下面的提示进行回答：\n" + String.join("\n", docs) + "\n原问题：" + prompt;
            log.info("构建RAG提示完成, chatId: {}, prompt:{}", chatId,ragPrompt);
            return ragPrompt;
        }
    }

    /**
     * 检查用户与会话关联关系
     *
     * @param chatId 会话ID
     * @param userId 用户ID
     * @return 返回是否关联
     */
    public boolean isUserConnectChat(String chatId, long userId) {
        log.debug("检查用户与会话关联关系, userId: {}, chatId: {}", userId, chatId);
        return userChatService.isUserConnectChat(chatId, userId);
    }

    /**
     * 创建新会话
     *
     * @param userId 用户ID
     * @return 返回新会话ID
     */
    @Override
    public String createNewChat(long userId) {
        log.info("开始创建新会话, userId: {}", userId);
        String chatId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        userChatService.save(new UserChat(userId, chatId));
        log.info("新会话创建完成, userId: {}, chatId: {}", userId, chatId);
        return chatId;
    }

    /**
     * AI打分
     *
     * @param prompt 试卷信息
     * @return 返回打分结果
     */
    @Override
    public String aiToScore(String prompt) {
        log.info("开始AI打分, prompt: {}", prompt);
        prompt = "根据下面的试卷信息以及学生的答题情况，修改answers列表下所有得分即score字段（简答题错了可以给过程分,其他题和答案不符就是0分）和totalScore（总得分）字段直接返回相同格式的信息，其他不变(尽量1分钟内回答)\n" + prompt;
        String result = chatClient.prompt(new Prompt(prompt)).call().content();
        log.info("AI打分完成：{}", result);
        return result;
    }

    /**
     * 解析题目
     *
     * @param prompt 题目内容
     * @return 返回解析结果流
     */
    @Override
    public Flux<String> analysis(String prompt) {
        log.info("开始解析题目, prompt: {}", prompt);
        prompt = "请解析下面这道题：\n" + prompt;
        return chatClient.prompt(new Prompt(prompt)).stream().content();
    }

    /**
     * 解析试卷题目
     *
     * @param paperId
     * @param questionId
     * @return
     */
    @Override
    public Flux<String> analysisExamPaperQuestion(String paperId, String questionId) {
        log.info("开始解析试卷题目, paperId: {}, questionId: {}", paperId, questionId);
        Result<Object> result = examClient.getQuestion(paperId, questionId);
        String prompt = JSON.toJSONString(result.getData());
        return analysis(prompt);
    }

    @Override
    public List<ChatHistoryVO> getChatHistory(long userId) {
        List<String> sessionIds = userChatService.getChatHistoryByUserId(userId);
        List<ChatHistoryVO> chatHistoryVOS = new ArrayList<>();
        sessionIds.forEach(sessionId -> {
            List<ChatMemory> cms = chatMemoryRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
            if(cms.isEmpty()){
                return;
            }
            ChatMemory first=cms.get(0);
            String title = first.getContent();
            if (title.length() > 10) {
                title = title.substring(0, 10) + "...";
            }
            ChatHistoryVO vo = ChatHistoryVO.builder()
                    .chatId(sessionId)
                    .title(title)
                    .createTime(first.getCreatedAt())
                    .build();
            chatHistoryVOS.add(vo);
        });
        return chatHistoryVOS;
    }

    @Override
    @Transactional
    public boolean deleteChat(String chatId, long userId) {
        userChatService.deleteChat(chatId, userId);
        chatMemoryRepository.deleteBySessionId(chatId);
        return true;
    }

    @Override
    public List<ChatMemory> getChatMemory(String chatId) {
        return chatMemoryRepository.findBySessionIdOrderByCreatedAtAsc(chatId);
    }

    @Override
    public Flux<String> summaryKnowledge(Long kpId) {
        return knowledgeClient.getKnowledgeReactive(kpId)
                .flatMapMany(knowledgeContent -> {
                    String prompt = String.format("""
                        请根据下面的内容生成一个简要的摘要，只关注文章内容，不要关注别的：
                         %s
                        """, knowledgeContent);
                    return chatClient.prompt(new Prompt(prompt)).stream().content();
                })
                .onErrorResume(e -> {
                    log.error("获取知识库内容或生成摘要时发生错误，kpId: {}", kpId, e);
                    return Flux.just("获取知识库内容时发生错误：" + e.getMessage());
                });
    }

    @Override
    public String analysisStudent(String prompt) {
        prompt = "请根据下面的某位学生的学习情况，给出几点建议，以MarkDown的格式返回(最好在一分钟内回答)：\n" + prompt;
        return chatClient.prompt(new Prompt(prompt)).call().content();
    }

    @Override
    public String generate(String prompt) {
        return chatClient.prompt(new Prompt(prompt)).call().content();
    }
}