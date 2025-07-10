package org.csu.ai.service;

import org.csu.ai.entity.po.ChatMemory;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2025-06-27
 */
public interface IChatMemoryService {

    /**
     * 根据会话id查询聊天记录
     * @param sessionId 会话id
     * @return List<ChatMemory>
     */
    List<ChatMemory> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    /**
     * 根据会话id删除聊天记录
     * @param conversationId 会话id
     * @return 成功返回true
     */
    boolean deleteBySessionId(String conversationId);

    /**
     * 保存聊天记录
     * @param memory 实体
     */
    void save(ChatMemory memory);
}
