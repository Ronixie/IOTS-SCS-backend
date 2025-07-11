package org.csu.homework.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class AssignmentsVO {
    /**
     * 作业唯一标识符
     */
    private long assignmentId;
    /**
     * 所属课程ID
     */
    private long courseId;
    /**
     * 所属课程名称
     */
    private String courseName;
    /**
     * 发布教师ID
     */
    private long teacherId;
    /**
     * 发布教师名称
     */
    private String teacherName;
    /**
     * 作业标题
     */
    private String title;
    /**
     * 截止日期
     */
    private LocalDateTime endDate;
    /**
     * 作业状态
     */
    private String status;
    /**
     * 作业总分
     */
    private BigDecimal score;
    /**
     * 最终提交时间
     */
    private LocalDateTime submitTime;

}
