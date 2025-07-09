package com.hwadee.IOTS_SCS.mapper;



import com.hwadee.IOTS_SCS.entity.POJO.Conversation;

import java.util.List;

public interface ConversationMapper {
    void insert(Conversation conversation);
    void update(Conversation conversation);
    void delete(Long conversationId);
    Conversation findById(Long conversationId);
    List<Conversation> findByUserId(Long userId);
    Conversation findByUserIds(Long userAId, Long userBId);
}
