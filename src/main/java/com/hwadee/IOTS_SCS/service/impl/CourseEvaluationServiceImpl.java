// 文件: com.hwadee.IOTS_SCS/service/impl/EvaluationServiceImpl.java
package com.hwadee.IOTS_SCS.service.impl;

import com.hwadee.IOTS_SCS.entity.DTO.EvaluationDetailDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Evaluation;
import com.hwadee.IOTS_SCS.mapper.CourseEvaluationMapper;
import com.hwadee.IOTS_SCS.service.CourseEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseEvaluationServiceImpl implements CourseEvaluationService {

    @Autowired
    private CourseEvaluationMapper courseEvaluationMapper;

    @Override
    public void addEvaluation(Evaluation evaluation) {
        // 插入评价数据（仅评价相关字段）
        courseEvaluationMapper.insert(evaluation);
    }

    @Override
    public List<Evaluation> getEvaluationsByCourseId(Long courseId) {
        // 查询仅评价数据的列表（用于后台管理）
        return courseEvaluationMapper.selectByCourseId(courseId);
    }

    @Override
    public Evaluation getEvaluationById(Long id) {
        // 查询单个评价（仅评价数据）
        return courseEvaluationMapper.selectById(id);
    }

    @Override
    public List<EvaluationDetailDTO> getEvaluationDetailsByCourseId(Long courseId) {
        // 查询评价详情（含课程名称和教师，用于前端展示）
        return courseEvaluationMapper.selectEvaluationDetailByCourseId(courseId);
    }
}