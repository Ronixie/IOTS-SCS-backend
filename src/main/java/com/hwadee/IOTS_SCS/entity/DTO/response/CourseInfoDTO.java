package com.hwadee.IOTS_SCS.entity.DTO.response;

import lombok.Data;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: CourseInfoDTO
* @Package: com.hwadee.IOTS_SCS.entity.DTO.response
* @Description: 课程详细信息
* @author qiershi
* @date 2025/7/9 11:35
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class CourseInfoDTO {
    private String courseName;
    private String status;
    private String teacherName;
    private String startDate;
    private String endDate;
    private String totalLessons;
    private double credit;
    private String description;
    private String coverImageUrl;
}