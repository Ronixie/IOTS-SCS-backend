package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.entity.DTO.ConversationDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Conversation;

import java.util.List;

public interface ConversationService {
    // 创建或获取已有会话
    Conversation createOrGetConversation(Long userAId, Long userBId);

    // 获取用户会话列表
    List<ConversationDTO> getUserConversations(Long userId);

    // 删除会话及消息
    void deleteConversation(Long conversationId);
}
