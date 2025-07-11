package org.csu.learn.entity.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
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
