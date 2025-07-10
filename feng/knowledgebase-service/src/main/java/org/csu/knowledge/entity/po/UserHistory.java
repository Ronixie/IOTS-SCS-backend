package org.csu.knowledge.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("knowledge_user_history")
public class UserHistory {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 知识点ID
     */
    private Long kpId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 浏览时间
     */
    private LocalDateTime viewedAt;

    /**
     * 停留时长（秒）
     */
    private Integer duration;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 