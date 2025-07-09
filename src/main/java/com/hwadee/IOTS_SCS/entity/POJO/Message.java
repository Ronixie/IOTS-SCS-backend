package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private Long msgId;          // 消息ID
    private Long conversationId; // 会话ID
    private Long senderId;       // 发送者ID
    private Long receiverId;     // 接收者ID
    private MessageType type;   // 消息类型
    private String content;      // 消息内容/文件路径/语音路径/视频路径/位置信息/富文本内容
    private Date sendTime;       // 发送时间
    private Boolean isRead;      // 是否已读
    private String extra;        // JSON格式的额外信息（可用于扩展）
}
