package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;

import java.util.Date;

/**
* @ProjectName: smart_study
* @Title: CourseDTO
* @Package: com.csu.smartstudy.entity.DTO
* @Description: 课程的简略信息的传递类
* @author qiershi
* @date 2025/7/1 9:55
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class CourseDTO {
    private Long courseId;
    private String courseName;
    private String teacherName;
    private int progress;
    private Date lastStudyTime;
}