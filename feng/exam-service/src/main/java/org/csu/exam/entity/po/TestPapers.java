package org.csu.exam.entity.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 试卷实体类
 * <p>表示考试系统中的试卷实体，包含试卷的基本信息和试题集合</p>
 *
 */
@Data
@Document(collection = "test_papers")
public class TestPapers {
    /**
     * 试卷ID
     */
    @Id
    private String id;

    /**
     * 试卷标题
     */
    private String title;

    /**
     * 试卷描述
     */
    private String description;

    /**
     * 所属课程
     */
    private long courseId;

    /**
     * 所属教师
     */
    private long teacherId;
    /**
     * 试题列表
     */
    private List<Question> questions;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 考试时间
     */
    private Date startTime;

    /**
     * 试卷状态
     */
    private String status;

    /**
     * 考试时长(分钟)
     */
    private int duration;

    /**
     * 试卷总分
     */
    private int totalScore;
    /**
     * 试题实体类
     * <p>表示试卷中的单个试题</p>
     */
    @Data
    public static class Question {
        /**
         * 试题ID
         */
        private String questionId;

        /**
         * 试题内容
         */
        private String content;

        /**
         * 试题类型
         */
        private String type;

        /**
         * 选项列表(选择题)
         */
        private List<Option> options;

        /**
         * 正确答案
         */
        private String answer;

        /**
         * 试题解析
         */
        private String analysis;

        /**
         * 分值
         */
        private int score;
        /**
         * 难度等级
         */
        private String difficulty;

        /**
         * 试题标签
         */
        private List<String> tags;


        @Data
        public static class Option {
            /**
             * 选项ID
             */
            private String optionId;
            /**
             * 选项内容
             */
            private String content;
        }
    }

}
