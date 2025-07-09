package com.hwadee.IOTS_SCS.entity.DTO;


import com.hwadee.IOTS_SCS.entity.POJO.MessageType;
import lombok.Data;

import java.util.Date;


@Data
public class MessageDTO {
    private Long msgId;
    private Long senderId;
    private String senderName;    // 发送者昵称
    private Long receiverId;
    private String avatar;        // 发送者头像
    private MessageType type;
    private String content;
    private Date sendTime;
    private Boolean isRead;
}
