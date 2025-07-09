package com.hwadee.IOTS_SCS.service.impl;


import com.hwadee.IOTS_SCS.entity.DTO.ConversationDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Conversation;
import com.hwadee.IOTS_SCS.entity.POJO.User;
import com.hwadee.IOTS_SCS.mapper.ConversationMapper;
import com.hwadee.IOTS_SCS.mapper.MessageMapper;
import com.hwadee.IOTS_SCS.mapper.UserMapper;
import com.hwadee.IOTS_SCS.service.ConversationService;
import com.hwadee.IOTS_SCS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {
    @Autowired
    private ConversationMapper conversationMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    @Override
    public Conversation createOrGetConversation(Long userAId, Long userBId) {
        // 检查是否已存在会话
        Conversation existConv = conversationMapper.findByUserIds(
                Math.min(userAId, userBId),
                Math.max(userAId, userBId)
        );
        if (existConv != null) {
            return existConv;
        }

        // 创建新会话
        Conversation newConv = new Conversation();
        newConv.setUserAId(Math.min(userAId, userBId));
        newConv.setUserBId(Math.max(userAId, userBId));
        newConv.setLastActiveTime(new Date());
        conversationMapper.insert(newConv);
        return newConv;
    }

    @Override
    public List<ConversationDTO> getUserConversations(Long userId) {
        // 获取原始会话列表
        List<Conversation> conversations = conversationMapper.findByUserId(userId);

        return conversations.stream().map(conv -> {
            ConversationDTO dto = new ConversationDTO();
            dto.setConversationId(conv.getConversationId());

            // 确定对方用户ID
            Long partnerId = conv.getUserAId().equals(userId) ?
                    conv.getUserBId() : conv.getUserAId();

            // 获取对方用户信息（需调用用户服务）
            User partner = userMapper.getUidUser(String.valueOf(partnerId));

            dto.setPartnerId(partnerId);
            dto.setPartnerName(partner.getName());
            dto.setAvatar(partner.getAvatarUrl());
            dto.setLastMessage(conv.getLastMessage());
            dto.setLastActiveTime(conv.getLastActiveTime());

            // 获取未读消息数
            dto.setUnreadCount(messageMapper.countUnreadMessages(
                    conv.getConversationId(), userId
            ));

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteConversation(Long conversationId) {
        // 删除所有相关消息
        messageMapper.deleteByConversationId(conversationId);
        // 删除会话
        conversationMapper.delete(conversationId);
    }
}
