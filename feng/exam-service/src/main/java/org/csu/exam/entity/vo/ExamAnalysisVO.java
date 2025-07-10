package org.csu.exam.entity.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamAnalysisVO {
    /**
     * 考试编号
     */
    private String id;
    /**
     * 考试名称
     */
    private String examName;
    /**
     * 考试总分
     */
    private Integer totalScore;
    /**
     * 考试得分
     */
    private Double score;
    /**
     * 课程Id
     */
    private Long courseId;
}
