package org.csu.homework.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SubmissionVO {
    /**
     * 学生信息
     */
    private Student student;
    /**
     * 提交唯一标识
     */
    private Long submissionId;
    /**
     * 提交次数
     */
    private Integer submissionNumber;

    /**
     * 主观题答案内容
     */
    private String answerContent;

    /**
     * 客观题答案 (JSON对象)
     */
    private String answersObjectiveJson;

    /**
     * 提交时间
     */
    private LocalDateTime submittedAt;

    /**
     * 提交文件附件列表 (JSON数组)
     */
    private String attachmentsJson;

    /**
     * 提交状态
     */
    private String status;

    /**
     * 作业得分
     */
    private BigDecimal score;

    /**
     * 教师评语
     */
    private String feedback;

    @Data
    public static class Student{
        /**
         * 学工号
         */
        private String studentId;
        /**
         * 学生姓名
         */
        private String studentName;
        /**
         * 班级
         */
        private String studentClass;
        /**
         * 学生头像
         */
        private String studentAvatar;
    }
}
