package org.csu.other.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
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
@TableName("student_course")
public class StudentCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选课记录唯一标识
     */
    @TableId(value = "enrollment_id")
    private Long enrollmentId;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 选课时间
     */
    private LocalDateTime date;

    /**
     * 最后停留的课时id
     */
    private Long lastAccessedLessonId;

    /**
     * 选课状态,  如"enrolled", "completed", "dropped"等
     */
    private String status;


}
