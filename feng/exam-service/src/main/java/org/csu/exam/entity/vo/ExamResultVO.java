package org.csu.exam.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试结果VO
 */
@Data
public class ExamResultVO {
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 考试ID
     */
    private String examId;
    
    /**
     * 原得分
     */
    private Double originalScore;
    
    /**
     * 新得分
     */
    private Double newScore;
    
    /**
     * 分数变化
     */
    private Double scoreChange;
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 正确题数
     */
    private Integer correctCount;
    
    /**
     * 正确率
     */
    private Double accuracy;
    
    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 题目结果列表
     */
    private List<QuestionResultVO> questions;
    
    /**
     * 题目结果VO
     */
    @Data
    public static class QuestionResultVO {
        /**
         * 题目ID
         */
        private String questionId;
        
        /**
         * 题目内容
         */
        private String content;
        
        /**
         * 题目类型
         */
        private String type;
        
        /**
         * 学生答案
         */
        private String studentAnswer;
        
        /**
         * 正确答案
         */
        private String correctAnswer;
        
        /**
         * 原得分
         */
        private Double originalScore;
        
        /**
         * 新得分
         */
        private Double newScore;
        
        /**
         * 满分
         */
        private Integer totalScore;
        
        /**
         * AI评语
         */
        private String aiComment;
    }
} 