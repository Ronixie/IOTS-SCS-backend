// 文件: com.hwadee.IOTS_SCS/dto/EvaluationDTO.java
package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;

@Data
public class EvaluationDTO {
    private Long courseId;
    private int contentEvaluation;
    private int serviceEvaluation;
    private int attitudeEvaluation;
    private int effectEvaluation;
    private String evaluationContent;
}