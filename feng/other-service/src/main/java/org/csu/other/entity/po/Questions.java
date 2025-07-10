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
@TableName("questions")
public class Questions implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 问题唯一标识符
     */
    @TableId(value = "question_id", type = IdType.INPUT)
    private Long questionId;

    /**
     * 所属课程ID
     */
    private Long courseId;

    /**
     * 所属课时ID
     */
    private Long lessonId;

    /**
     * 提问学生ID
     */
    private Long studentId;

    /**
     * 问题标题
     */
    private String title;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 附件列表 (JSON数组)
     */
    private String attachmentsJson;

    /**
     * 问题状态 (e.g., open, answered, closed)
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;


}
