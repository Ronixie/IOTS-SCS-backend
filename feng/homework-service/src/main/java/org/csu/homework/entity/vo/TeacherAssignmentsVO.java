package org.csu.homework.entity.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeacherAssignmentsVO {
    private Long assignmentId;

    /**
     * 所属课程ID
     */
    private Long courseId;
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 发布教师ID
     */
    private Long teacherId;

    /**
     * 作业标题
     */
    private String title;

    /**
     * 作业描述/题目内容
     */
    private String content;

    /**
     * 最大提交次数
     */
    private Integer maxNumberSubmissions;

    /**
     * 截止日期
     */
    private LocalDateTime endDate;

    /**
     * 附件列表 (JSON数组)
     */
    private String attachmentsJson;
    /**
     * 状态
     */
    private String status;
}
