package org.csu.learn.entity.vo;


import lombok.Data;

@Data
public class KnowledgeAnalysis {
    /**
     * 知识库id
     */
    private Long id;
    /**
     * 知识库标题
     */
    private String title;
    /**
     * 知识库点赞数
     */
    private int likeCount;
    /**
     * 知识库评论数
     */
    private int commentCount;
    /**
     * 知识库浏览数
     */
    private int viewCount;
    /**
     * 知识库收藏数
     */
    private int favoriteCount;
}
