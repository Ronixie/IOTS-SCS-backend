package org.csu.ai.service.impl;

import lombok.RequiredArgsConstructor;
import org.csu.ai.entity.po.ChatMemory;
import org.csu.ai.repository.ChatMemoryRepository;
import org.csu.ai.service.IChatMemoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2025-06-27
 */
@Service
@RequiredArgsConstructor
public class ChatMemoryServiceImpl implements IChatMemoryService {
    private final ChatMemoryRepository repository;
    /**
     * 根据会话id查询聊天记录
     * @param sessionId 会话id
     * @return
     */
    @Override
    public List<ChatMemory> findBySessionIdOrderByCreatedAtAsc(String sessionId) {
        return repository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    /**
     * 根据会话id删除聊天记录
     * @param conversationId 会话id
     * @return
     */
    @Override
    public boolean deleteBySessionId(String conversationId) {
        try{
            repository.deleteBySessionId(conversationId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public void save(ChatMemory memory) {
        repository.save(memory);
    }
}
