package com.hwadee.IOTS_SCS.entity.DTO.request;

@lombok.Data
public class CreateReplyReq {
    private Long postId;
    private Long userId;
    private String content;
}
