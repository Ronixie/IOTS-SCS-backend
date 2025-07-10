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
 * 答案表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("answer")
public class Answer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 试卷id，复合主键之一
     */
    @MppMultiId
    @TableField(value = "paper_id")
    private String paperId;

    /**
     * 问题id，复合主键之一
     */
    @TableField(value = "question_id")
    private String questionId;

    /**
     * 用户id，复合主键之一
     */
    @TableField(value = "user_id")
    private long userId;

    /**
     * 答案
     */
    private String answer;

    /**
     * 回答时间
     */
    private LocalDateTime answerTime;

    /**
     * 得分
     */
    private double score;
}
