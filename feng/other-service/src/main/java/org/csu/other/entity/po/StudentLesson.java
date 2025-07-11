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
 * @since 2025-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("student_lesson")
public class StudentLesson implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学习进度唯一标识
     */
    @TableId(value = "progress_id", type = IdType.AUTO)
    private Long progressId;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 课时id
     */
    private Long lessonId;

    /**
     * 是否完成
     */
    private Boolean isCompleted;

    /**
     * 观看时长(秒)
     */
    private Integer viewSeconds;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessedAt;


}
