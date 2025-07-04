package com.hwadee.IOTS_SCS.entity.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: EvaluationReportDTO
* @Package: com.hwadee.IOTS_SCS.entity.DTO.response
* @Description: 学习效果报告DTO
* @author qiershi
* @date 2025/7/3 15:03
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class EvaluationReportDTO {
    private int      totalCourses;       // 选课总数
    private int      completedCourses;   // 已结课
    private int      inProgressCourses;  // 进行中
    private int      totalLessons;       // 所有课程小节总数
    private int      completedLessons;   // 已完成小节
    private double   averageProgress;    // 平均进度（0‑100）
    private LocalDateTime periodFrom;    // 统计起点
    private LocalDateTime periodTo;      // 统计终点
}