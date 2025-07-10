// 文件: com.hwadee.IOTS_SCS/controller/EvaluationController.java
package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.EvaluationDetailDTO;
import com.hwadee.IOTS_SCS.entity.DTO.EvaluationDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Evaluation;
import com.hwadee.IOTS_SCS.service.CourseEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class CourseEvaluationController {

    @Autowired
    private CourseEvaluationService courseEvaluationService;

    /**
     * 添加评价（前端提交评价时调用）
     */
    @PostMapping
    public CommonResult addEvaluation(@Validated @RequestBody EvaluationDTO evaluationDTO) {
        // 将 DTO 转换为 POJO（仅评价数据）
        Evaluation evaluation = convertToPOJO(evaluationDTO);
        courseEvaluationService.addEvaluation(evaluation);
        return CommonResult.success("评价提交成功");
    }

    /**
     * 获取某课程的评价列表（后台管理使用，仅评价数据）
     */
    @GetMapping("/course/{courseId}")
    public CommonResult<List<Evaluation>> getEvaluationsByCourse(@PathVariable Long courseId) {
        List<Evaluation> evaluations = courseEvaluationService.getEvaluationsByCourseId(courseId);
        return CommonResult.success(evaluations);
    }

    /**
     * 获取某课程的评价详情（前端展示使用，含课程名称和教师）
     */
    @GetMapping("/details/{courseId}")
    public CommonResult<List<EvaluationDetailDTO>> getEvaluationDetailsByCourse(@PathVariable Long courseId) {
        List<EvaluationDetailDTO> details = courseEvaluationService.getEvaluationDetailsByCourseId(courseId);
        return CommonResult.success(details);
    }

    /**
     * DTO 转 POJO（仅评价数据）
     */
    private Evaluation convertToPOJO(EvaluationDTO dto) {
        Evaluation evaluation = new Evaluation();
        evaluation.setCourseId(dto.getCourseId());
        evaluation.setContentEvaluation(dto.getContentEvaluation());
        evaluation.setServiceEvaluation(dto.getServiceEvaluation());
        evaluation.setAttitudeEvaluation(dto.getAttitudeEvaluation());
        evaluation.setEffectEvaluation(dto.getEffectEvaluation());
        evaluation.setEvaluationContent(dto.getEvaluationContent());
        return evaluation;
    }
}