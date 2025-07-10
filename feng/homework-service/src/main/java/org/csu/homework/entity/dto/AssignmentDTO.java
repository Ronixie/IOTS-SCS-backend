package org.csu.homework.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentDTO {
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
     * 最大批改机会次数
     */
    private Integer maxChanceAttempts;

    /**
     * 截止日期
     */
    private LocalDateTime endDate;

    /**
     * 附件列表 (JSON数组)
     */
    private String attachmentsJson;
}
