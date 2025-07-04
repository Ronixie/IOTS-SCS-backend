package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: Enrollment
* @Package: com.hwadee.IOTS_SCS.entity.POJO
* @Description: 连接student_course表，表明选课关系
* @author qiershi
* @date 2025/7/4 15:28
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class Enrollment {
    private Long enrollmentId;
    private Long studentId;
    private Long courseId;
    private Long lastAccessedLessonId;
    private String status;
}