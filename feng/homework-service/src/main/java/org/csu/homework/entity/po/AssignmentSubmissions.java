package org.csu.homework.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("assignment_submissions")
public class AssignmentSubmissions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业提交唯一标识符
     */
    @TableId(value = "submission_id", type = IdType.INPUT)
    private Long submissionId;

    /**
     * 所属作业ID
     */
    private Long assignmentId;

    /**
     * 提交学生ID
     */
    private Long studentId;

    /**
     * 提交次数 (第几次提交)
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
     * 提交状态 (e.g., submitted, graded, returned)
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

    /**
     * 批改教师ID
     */
    private Long gradedById;

    /**
     * 批改时间
     */
    private LocalDateTime gradedAt;
}
