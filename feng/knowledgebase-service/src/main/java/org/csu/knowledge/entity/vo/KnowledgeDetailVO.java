package org.csu.knowledge.entity.vo;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeDetailVO {
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
     * 创建者
     */
    private KnowledgeListVO.Creator creator;

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

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 收藏数
     */
    private Integer favoriteCount;

    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;

    /**
     * 当前用户是否已收藏
     */
    private Boolean isFavorited;
}
