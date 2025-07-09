package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

import java.util.Date;

@Data
public class Conversation {
    private Long conversationId;     // 会话ID
    private Long userAId;            // 用户A ID
    private Long userBId;            // 用户B ID
    private Date lastActiveTime;     // 最后活动时间
    private String lastMessage;      // 最后一条消息内容
}
