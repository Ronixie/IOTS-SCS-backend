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
@TableName("notes")
public class Notes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 笔记唯一标识符
     */
    @TableId(value = "note_id", type = IdType.INPUT)
    private Long noteId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 所属课程ID
     */
    private Long courseId;

    /**
     * 所属课时ID
     */
    private Long lessonId;

    /**
     * 笔记标题
     */
    private String title;

    /**
     * 笔记内容
     */
    private String content;

    /**
     * 附件列表 (JSON数组)
     */
    private String attachmentsJson;

    /**
     * 可见性 (e.g., private, public, course_only)
     */
    private String visibility;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;


}
