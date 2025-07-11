package org.csu.knowledge.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryVO {
    /**
     * 历史记录ID
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