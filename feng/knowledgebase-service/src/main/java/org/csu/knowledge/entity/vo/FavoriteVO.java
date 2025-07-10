package org.csu.knowledge.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoriteVO {
    /**
     * 收藏记录ID
     */
    private Long id;

    /**
     * 知识点ID
     */
    private Long kpId;

    /**
     * 知识点标题
     */
    private String title;

    /**
     * 知识点简单内容
     */
    private String simpleContent;

    /**
     * 创建者信息
     */
    private KnowledgeListVO.Creator creator;

    /**
     * 标签列表
     */
    private String tags;

    /**
     * 收藏时间
     */
    private LocalDateTime favoritedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 