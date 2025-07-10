package org.csu.exam.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 考试列表返回视图对象
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ExamListVO {
    /**
     * 考试ID
     */
    private String id;
    /**
     * 考试标题
     */
    private String title;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 考试时长(分钟)
     */
    private int duration;
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 考试状态
     */
    private String status;
    /**
     * 考试成绩
     */
    private double score;
    /**
     * 答题个数
     */
    private int answerNum;
    /**
     * 题目数量
     */
    private int questionNum;
    /**
     * 正确题目数
     */
    private int correctNum;
    /**
     * 考试总分
     */
    private int totalScore;

}