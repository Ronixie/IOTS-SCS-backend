package org.csu.homework.entity.vo;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AssignmentAnalysisVO {
    /**
     * 作业ID
     */
    private Long id;

    /**
     * 所属课程ID
     */
    private Long courseId;

    /**
     * 作业标题
     */
    private String title;

    /**
     * 作业得分
     */
    private BigDecimal score;
}
