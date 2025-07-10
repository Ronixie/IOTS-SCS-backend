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
 * @since 2025-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("courses")
public class Courses implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程唯一标识
     */
    @TableId(value = "course_id")
    private Long courseId;

    /**
     * 课程名
     */
    private String courseName;

    /**
     * 授课老师id
     */
    private Long teacherId;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 课程封面图片url
     */
    private String coverImageUrl;


}
