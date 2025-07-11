package com.hwadee.IOTS_SCS.entity.DTO.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CreateNoticeReq {
    private Long courseId;
    private String title;
    private String content;
    private Long userId;
    private Date noticeTime;
    private List<Long> receiverIds;
}
