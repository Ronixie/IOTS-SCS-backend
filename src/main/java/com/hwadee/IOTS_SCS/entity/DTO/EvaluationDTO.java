// 文件: com.hwadee.IOTS_SCS/dto/EvaluationDTO.java
package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;

@Data
public class EvaluationDTO {
    private Long courseId;


    private Integer contentEvaluation;


    private Integer serviceEvaluation;


    private Integer attitudeEvaluation;


    private Integer effectEvaluation;


    private String evaluationContent;
}