package org.csu.exam.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生考试详情VO
 */
@Data
public class StudentExamDetailVO {
    /**
     * 学生ID
     */
    private Long studentId;
    /**
     * 学生学号
     */
    private String studentNumber;
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 考试ID
     */
    private String examId;
    
    /**
     * 考试标题
     */
    private String examTitle;
    
    /**
     * 得分
     */
    private Double score;
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
    
    /**
     * 题目详情列表
     */
    private List<QuestionDetailVO> questions;
    
    /**
     * 题目详情VO
     */
    @Data
    public static class QuestionDetailVO {
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
         * 得分
         */
        private Double score;
        
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