package org.csu.homework.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2025-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("assignments")
public class Assignments implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业唯一标识符
     */
    @TableId(value = "assignment_id", type = IdType.INPUT)
    private Long assignmentId;

    /**
     * 所属课程ID
     */
    private Long courseId;

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
