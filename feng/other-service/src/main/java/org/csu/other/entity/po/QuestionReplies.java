package org.csu.other.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2025-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("question_replies")
public class QuestionReplies implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 回复唯一标识符
     */
    @TableId(value = "reply_id", type = IdType.INPUT)
    private Long replyId;

    /**
     * 所属问题ID
     */
    private Long questionId;

    /**
     * 回复人ID
     */
    private Long replierId;

    /**
     * 回复人类型 (e.g., teacher, student)
     */
    private String replierType;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;


}
