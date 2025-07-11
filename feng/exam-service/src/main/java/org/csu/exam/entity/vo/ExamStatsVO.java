package org.csu.exam.entity.vo;

import lombok.Data;

/**
 * 考试统计VO
 */
@Data
public class ExamStatsVO {
    /**
     * 考试ID
     */
    private String examId;
    
    /**
     * 考试标题
     */
    private String examTitle;
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 题目数量
     */
    private Integer questionNum;
    
    /**
     * 参与学生总数
     */
    private Integer totalStudents;
    
    /**
     * 平均分
     */
    private Double averageScore;
    
    /**
     * 最高分
     */
    private Double highestScore;
    
    /**
     * 最低分
     */
    private Double lowestScore;
    
    /**
     * 及格率
     */
    private Double passRate;
    
    /**
     * 优秀率（90分以上）
     */
    private Double excellentRate;
    
    /**
     * 良好率（80-89分）
     */
    private Double goodRate;
    
    /**
     * 中等率（70-79分）
     */
    private Double mediumRate;
    
    /**
     * 及格率（60-69分）
     */
    private Double passRateDetail;
    
    /**
     * 不及格率（60分以下）
     */
    private Double failRate;
} 