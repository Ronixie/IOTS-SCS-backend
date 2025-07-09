package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class NoticeDetailDTO {
    private Long noticeId;
    private Long CourseId;
    private String courseName;
    private Long userId;
    private String userName;
    private String avatar;
    private String noticeTitle;
    private String noticeContent;
    private Date noticeTime;
    private List<Long> receiverIds;
    private List<Long> readUserIds;
}
