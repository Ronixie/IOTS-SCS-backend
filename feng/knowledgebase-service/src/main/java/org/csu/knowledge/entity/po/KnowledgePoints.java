package org.csu.knowledge.entity.po;

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
 * @since 2025-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("knowledge_points")
public class KnowledgePoints implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 知识点唯一标识符
     */
    @TableId(value = "kp_id")
    private Long kpId;

    /**
     * 知识点标题
     */
    private String title;

    /**
     * 知识点内容
     */
    private String content;

    /**
     * 创建者ID
     */
    private Long authorId;

    /**
     * 附件列表 (JSON数组)
     */
    private String attachmentsJson;

    /**
     * 标签列表 (JSON数组)
     */
    private String tags;

    /**
     * 知识点状态 (e.g., draft, published)
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
