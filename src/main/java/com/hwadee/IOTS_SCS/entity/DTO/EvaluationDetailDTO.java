// 文件: com.hwadee.IOTS_SCS/dto/EvaluationDetailDTO.java
package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;

@Data
public class EvaluationDetailDTO {
    private Long evaluationId;
    private Long courseId;
    private String courseName;   // 课程名称
    private String teacher;      // 授课教师
    private int contentEvaluation;
    private int serviceEvaluation;
    private int attitudeEvaluation;
    private int effectEvaluation;
    private String evaluationContent;
}