package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class ConversationDTO {
    private Long conversationId;
    private Long partnerId;       // 对方用户ID
    private String partnerName;   // 对方昵称
    private String avatar;        // 对方头像
    private String lastMessage;   // 最后消息预览
    private Date lastActiveTime;  // 最后活动时间
    private Integer unreadCount;  // 未读消息数
}
