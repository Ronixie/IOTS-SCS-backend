package com.hwadee.IOTS_SCS.entity.DTO;


import com.hwadee.IOTS_SCS.entity.POJO.MessageType;
import lombok.Data;

@Data
public class SendMessageReq {
    private Long conversationId;
    private Long senderId;
    private Long receiverId;
    private MessageType type;
    private String content;
}
