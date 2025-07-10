package org.csu.knowledge.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    /**
     * 评论ID
     */
    private Long id;

    /**
     * 知识点ID
     */
    private Long kpId;

    /**
     * 评论者信息
     */
    private KnowledgeListVO.Creator user;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论ID（用于回复功能）
     */
    private Long parentId;

    /**
     * 子评论列表
     */
    private List<CommentVO> replies;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 