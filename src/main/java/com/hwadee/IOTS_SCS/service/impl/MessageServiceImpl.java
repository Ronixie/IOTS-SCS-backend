package com.hwadee.IOTS_SCS.service.impl;

import com.hwadee.IOTS_SCS.entity.POJO.MessageType;
import com.hwadee.IOTS_SCS.entity.DTO.MessageDTO;
import com.hwadee.IOTS_SCS.entity.DTO.SendMessageReq;
import com.hwadee.IOTS_SCS.entity.POJO.Conversation;
import com.hwadee.IOTS_SCS.entity.POJO.Message;
import com.hwadee.IOTS_SCS.entity.POJO.User;
import com.hwadee.IOTS_SCS.mapper.ConversationMapper;
import com.hwadee.IOTS_SCS.mapper.MessageMapper;
import com.hwadee.IOTS_SCS.mapper.UserMapper;
import com.hwadee.IOTS_SCS.service.MessageService;
import com.hwadee.IOTS_SCS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private ConversationMapper conversationMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    @Override
    public Message sendMessage(SendMessageReq req) {
        // 创建消息对象
        Message message = new Message();
        message.setConversationId(req.getConversationId());
        message.setSenderId(req.getSenderId());
        message.setReceiverId(req.getReceiverId());
        message.setType(req.getType());
        message.setContent(req.getContent());
        message.setSendTime(new Date());
        message.setIsRead(false); // 初始未读

        messageMapper.insert(message);

        // 更新会话最后活动时间和最后消息
        Conversation conv = conversationMapper.findById(req.getConversationId());
        conv.setLastActiveTime(new Date());
        conv.setLastMessage(generatePreview(req.getType(), req.getContent()));
        conversationMapper.update(conv);

        return message;
    }

    @Override
    public List<MessageDTO> getConversationMessages(Long conversationId, Long currentUserId) {
        List<Message> messages = messageMapper.findByConversationId(conversationId);

        // 将消息标记为已读
        markMessagesAsRead(conversationId, currentUserId);

        return messages.stream().map(msg -> {
            MessageDTO dto = new MessageDTO();
            dto.setMsgId(msg.getMsgId());
            dto.setSenderId(msg.getSenderId());
            dto.setReceiverId(msg.getReceiverId());
            dto.setType(msg.getType());
            dto.setContent(msg.getContent());
            dto.setSendTime(msg.getSendTime());
            dto.setIsRead(msg.getIsRead());

            // 获取发送者信息
            User sender = userMapper.getUidUser(String.valueOf(msg.getSenderId()));
            dto.setSenderName(sender.getName());
            dto.setAvatar(sender.getAvatarUrl());

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void markMessagesAsRead(Long conversationId, Long userId) {
        messageMapper.markAsRead(conversationId, userId);
    }

    // 消息预览
    @Override
    public String generatePreview(MessageType type, String content) {
        return switch (type) {
            case TEXT -> content.length() > 20 ? content.substring(0, 20) + "..." : content;
            case IMAGE -> "[图片]";
            case VOICE -> "[语音]";
            case VIDEO -> "[视频]";
            case FILE -> "[文件]";
            case LOCATION -> "[位置]";
            case RICH_TEXT -> "[富文本]";
            default -> content;
        };
    }

    @Override
    public MessageDTO convertToDTO(Message msg) {
        // 实现消息到DTO的转换（包含用户信息）
        MessageDTO dto = new MessageDTO();
        dto.setMsgId(msg.getMsgId());
        dto.setSenderId(msg.getSenderId());
        dto.setType(msg.getType());
        dto.setContent(msg.getContent());
        dto.setSendTime(msg.getSendTime());
        dto.setIsRead(msg.getIsRead());

        // 获取发送者信息
        User sender = userMapper.getUidUser(String.valueOf(msg.getSenderId()));
        dto.setSenderName(sender.getName());
        dto.setAvatar(sender.getAvatarUrl());

        return dto;
    }
}
