package org.csu.learn.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
