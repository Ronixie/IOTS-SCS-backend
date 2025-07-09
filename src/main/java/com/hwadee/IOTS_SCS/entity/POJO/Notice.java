package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Notice {
    private Long noticeId;
    private Long CourseId;
    private Long userId;
    private String noticeTitle;
    private String noticeContent;
    private Date noticeTime;
    private Boolean isSend;
    private List<Long> receiverIds;
    private List<Long> readUserIds;
}
