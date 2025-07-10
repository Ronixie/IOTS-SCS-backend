package com.hwadee.IOTS_SCS.entity.DTO.request;

import lombok.Data;

@Data
public class CreateConversationReq {
    private Long userAId;
    private Long userBId;
}
