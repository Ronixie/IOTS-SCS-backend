package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

import java.util.Date;

@Data

public class Reply {
    private Long replyId;
    private Long ofPostId;
    private Long userId;
    private String content;
    private Date createTime;
}

