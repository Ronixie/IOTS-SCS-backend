package org.csu.homework.entity.vo;

import lombok.Data;
import org.csu.homework.entity.po.AssignmentSubmissions;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentDetailVO {
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
     * 作业所属教师
     */
    private Teacher teacher;
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
     * 提交历史
     */
    private List<AssignmentSubmissions> submissions;
    /**
     * 作业状态
     */
    private String status;
    /**
     * 教师信息
     */
    @Data
    public static class Teacher {
        /**
         * 教师ID
         */
        private long teacherId;
        /**
         * 教师姓名
         */
        private String teacherName;
        /**
         * 教师头像URL
         */
        private String teacherAvatar;
        /**
         * 教师邮箱
         */
        private String teacherEmail;
    }
}
