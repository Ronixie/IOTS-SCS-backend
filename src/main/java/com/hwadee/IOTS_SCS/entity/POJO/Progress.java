package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

import java.util.Date;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: Progress
* @Package: com.hwadee.IOTS_SCS.entity.POJO
* @Description: 连接student_course_progress表，表明学生学习进度
* @author qiershi
* @date 2025/7/4 15:32
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class Progress {
    private Long studentId;
    private Long courseId;
    private int totalLessons;
    private int completedLessons;
    private int progress;
    private Date lastUpdatedAt;
}