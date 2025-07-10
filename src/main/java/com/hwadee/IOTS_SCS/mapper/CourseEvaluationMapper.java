package com.hwadee.IOTS_SCS.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwadee.IOTS_SCS.entity.DTO.EvaluationDetailDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Evaluation;

import java.util.List;

public interface CourseEvaluationMapper{
    List<EvaluationDetailDTO> selectEvaluationDetailByCourseId(Long courseId);
    void insert(Evaluation evaluation);
    List<Evaluation> selectByCourseId(Long courseId);
    List<Evaluation> selectAll();
    Evaluation selectById(Long id);
    double calculateAverageScore(Long courseId);
}
