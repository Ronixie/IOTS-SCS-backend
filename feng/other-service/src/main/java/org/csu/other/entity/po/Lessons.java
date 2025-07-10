package org.csu.other.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("lessons")
public class Lessons implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课时唯一标识
     */
    @TableId(value = "lesson_id", type = IdType.AUTO)
    private Long lessonId;

    /**
     * 所属课程
     */
    private Long courseId;

    /**
     * 课时名
     */
    private String lessonName;

    /**
     * 课时类型
     */
    private String lessonType;

    /**
     * 课时内容url
     */
    private String contentUrl;

    /**
     * 文本内容
     */
    private String textContent;

    /**
     * 再课程中的顺序
     */
    private Integer order;


}
