// 文件: com.hwadee.IOTS_SCS/service/EvaluationService.java
package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.entity.DTO.EvaluationDetailDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Evaluation;
import java.util.List;

public interface CourseEvaluationService {
    /**
     * 添加评价
     */
    void addEvaluation(Evaluation evaluation);

    /**
     * 根据课程ID查询评价列表（仅评价数据）
     */
    List<Evaluation> getEvaluationsByCourseId(Long courseId);

    /**
     * 根据评价ID查询单个评价（仅评价数据）
     */
    Evaluation getEvaluationById(Long id);

    /**
     * 根据课程ID查询评价详情（含课程名称和教师）
     */
    List<EvaluationDetailDTO> getEvaluationDetailsByCourseId(Long courseId);
}