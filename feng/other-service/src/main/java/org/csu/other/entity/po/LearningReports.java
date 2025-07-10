package org.csu.other.entity.po;

import java.math.BigDecimal;
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
@TableName("learning_reports")
public class LearningReports implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报告唯一标识符
     */
    @TableId(value = "report_id", type = IdType.INPUT)
    private Long reportId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 报告周期 (e.g., weekly, monthly, term, overall)
     */
    private String period;

    /**
     * 报告生成时间
     */
    private LocalDateTime generatedAt;

    /**
     * 总学习进度百分比
     */
    private BigDecimal overallProgress;

    /**
     * 平均成绩
     */
    private BigDecimal averageScore;

    /**
     * 学习活跃度指标 (JSON对象)
     */
    private String engagementMetricsJson;

    /**
     * 各课程表现 (JSON数组)
     */
    private String performanceByCourseJson;

    /**
     * 技能掌握情况 (JSON数组)
     */
    private String skillMasteryJson;

    /**
     * 个性化学习建议 (JSON数组)
     */
    private String recommendationsJson;


}
