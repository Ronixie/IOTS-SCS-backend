package org.csu.ai.service;

import org.csu.ai.entity.po.ChatMemory;
import org.csu.ai.entity.vo.ChatHistoryVO;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    /**
     * ai对话
     * @param chatId 会话id
     * @param prompt 用户输入
     * @return ai流式回复
     */
    Flux<String> chat(String chatId, String prompt);

    /**
     * 包含文件的对话
     * @param chatId 会话id
     * @param prompt 用户输入
     * @param files 文件列表
     * @return
     */
    Flux<String> chat(String chatId, String prompt, List<String> files);

    /**
     * 判断用户和该会话是否有关
     * @param chatId 会话id
     * @param userId 用户id
     * @return true or false
     */
    boolean isUserConnectChat(String chatId,long userId);

    /**
     * 创建新的会话
     * @param userId 用户id
     * @return 会话id
     */
    String createNewChat(long userId);

    /**
     * ai评分
     * @param prompt
     * @return
     */
    String aiToScore(String prompt);

    /**
     * 题目解析
     * @param prompt
     * @return
     */
    Flux<String> analysis(String prompt);

    /**
     * 试卷题目解析
     * @param paperId
     * @param questionId
     * @return
     */
    Flux<String> analysisExamPaperQuestion(String paperId, String questionId);

    /**
     * 获取聊天记录
     * @param userId 用户id
     * @return
     */
    List<ChatHistoryVO> getChatHistory(long userId);

    boolean deleteChat(String chatId, long userId);

    List<ChatMemory> getChatMemory(String chatId);

    Flux<String> summaryKnowledge(Long kpId);

    String analysisStudent(String prompt);

    String generate(String prompt);
}
