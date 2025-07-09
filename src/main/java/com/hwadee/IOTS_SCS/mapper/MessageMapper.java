package com.hwadee.IOTS_SCS.mapper;


import com.hwadee.IOTS_SCS.entity.POJO.Message;

import java.util.List;

public interface MessageMapper {
    void insert(Message message);
    void deleteByConversationId(Long conversationId);
    List<Message> findByConversationId(Long conversationId);
    int countUnreadMessages(Long conversationId, Long userId);
    void markAsRead(Long conversationId, Long userId);
}