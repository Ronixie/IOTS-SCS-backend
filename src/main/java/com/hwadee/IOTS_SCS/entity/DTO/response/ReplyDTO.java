package com.hwadee.IOTS_SCS.entity.DTO.response;

import lombok.Data;

import java.util.Date;

@Data
public class ReplyDTO {
    private Long replyId;
    private Long ofPostId;
    private Long userId;
    private String userName;
    private String avatar;
    private String content;
    private Date createTime;
}