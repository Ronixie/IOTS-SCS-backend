package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.entity.DTO.MessageDTO;
import com.hwadee.IOTS_SCS.entity.DTO.SendMessageReq;
import com.hwadee.IOTS_SCS.entity.POJO.Message;
import com.hwadee.IOTS_SCS.entity.POJO.MessageType;

import java.util.List;

public interface MessageService {
    Message sendMessage(SendMessageReq req);

    // 获取会话消息历史
    List<MessageDTO> getConversationMessages(Long conversationId, Long currentUserId);

    // 将消息标记为已读
    void markMessagesAsRead(Long conversationId, Long userId);
    String generatePreview(MessageType type, String content);

    MessageDTO convertToDTO(Message msg);
}
