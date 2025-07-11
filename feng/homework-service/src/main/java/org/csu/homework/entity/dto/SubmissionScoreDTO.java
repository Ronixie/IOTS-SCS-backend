package org.csu.homework.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubmissionScoreDTO {
    private BigDecimal score;
    private String feedback;
}
