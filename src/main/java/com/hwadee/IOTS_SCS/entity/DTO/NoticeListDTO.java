package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class NoticeListDTO {
    private Long noticeId;
    private String title;
    private String preview;       // 内容预览
    private Long courseId;
    private String courseName;
    private Date noticeTime;
    private Boolean isRead;
}
