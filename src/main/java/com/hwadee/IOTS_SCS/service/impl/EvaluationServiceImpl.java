package com.hwadee.IOTS_SCS.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.EvaluationReportDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Enrollment;
import com.hwadee.IOTS_SCS.entity.POJO.Progress;
import com.hwadee.IOTS_SCS.mapper.EvaluationMapper;
import com.hwadee.IOTS_SCS.service.EvaluationService;
import com.hwadee.IOTS_SCS.service.LogService;
import org.apache.ibatis.ognl.Evaluation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: EvaluationServiceImpl
* @Package: com.hwadee.IOTS_SCS.service.impl
* @Description: 学习效果评价服务类的具体实现
* @author qiershi
* @date 2025/7/2 8:18
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Service
public class EvaluationServiceImpl implements EvaluationService {

    @Autowired
    private LogService logService;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Override
    public CommonResult<EvaluationReportDTO> generateReport(String period, String uid) {
        EvaluationReportDTO dto = new EvaluationReportDTO();

        LocalDateTime time = null;

        switch (period) {
            case "weekly":
                time = LocalDateTime.now().minusWeeks(1);
                break;
            case "monthly":
                time = LocalDateTime.now().minusMonths(1);
                break;
            case "yearly":
                time = LocalDateTime.now().minusYears(1);
                break;
            default:
                time = LocalDateTime.now().minusMonths(3);
        }

        // 查数据库，获得数据
        IPage<Enrollment> enrollment = new Page<>();
        IPage<Progress> progress = new Page<>();

        evaluationMapper.selectEnrollment(enrollment, uid, time);
        evaluationMapper.selectProgress(progress, uid, time);

        // ------------------------- 分析数据 -------------------------
        List<Enrollment> enrollments = enrollment.getRecords();   // 选课记录
        List<Progress>   progresses  = progress.getRecords();     // 进度记录

        // 1. 课程维度统计
        int totalCourses      = enrollments.size();
        int completedCourses  = (int) enrollments.stream()
                .filter(e -> "completed".equalsIgnoreCase(e.getStatus()))
                .count();
        int inProgressCourses = (int) enrollments.stream()
                .filter(e -> "in_progress".equalsIgnoreCase(e.getStatus()))
                .count();

        // 2. 小节 / 进度维度统计
        int totalLessons      = progresses.stream()
                .mapToInt(Progress::getTotalLessons)
                .sum();
        int completedLessons  = progresses.stream()
                .mapToInt(Progress::getCompletedLessons)
                .sum();
        double averageProgress = progresses.isEmpty()
                ? 0.0
                : progresses.stream()
                .mapToInt(Progress::getProgress)
                .average()
                .orElse(0.0);

        // 3. 将数据填充进 DTO
        dto.setTotalCourses(totalCourses);
        dto.setCompletedCourses(completedCourses);
        dto.setInProgressCourses(inProgressCourses);

        dto.setTotalLessons(totalLessons);
        dto.setCompletedLessons(completedLessons);
        dto.setAverageProgress(
                Math.round(averageProgress * 10.0) / 10.0   // 保留 1 位小数，可选
        );

        dto.setPeriodFrom(time);               // 统计起点
        dto.setPeriodTo(LocalDateTime.now());  // 统计终点
        // -----------------------------------------------------------

        return CommonResult.success(dto);
    }

}