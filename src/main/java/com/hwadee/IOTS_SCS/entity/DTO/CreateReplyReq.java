package com.hwadee.IOTS_SCS.entity.DTO;

import java.util.Date;

@lombok.Data
public class CreateReplyReq {
    private Long postId;
    private Long userId;
    private String content;
    private Date createTime;
}
