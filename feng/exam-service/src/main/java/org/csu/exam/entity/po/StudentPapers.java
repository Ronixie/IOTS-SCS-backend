package org.csu.exam.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 学生与试卷的关联表
 * </p>
 *
 * @author 
 * @since 2025-06-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("student_papers")
public class StudentPapers implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @MppMultiId
    @TableField(value = "student_id")
    private long studentId;

    @MppMultiId
    @TableField(value = "paper_id")
    private String paperId;

    /**
     * 状态，0为未完成，1为完成
     */
    private Integer status;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 总分
     */
    private double totalScore;

}
