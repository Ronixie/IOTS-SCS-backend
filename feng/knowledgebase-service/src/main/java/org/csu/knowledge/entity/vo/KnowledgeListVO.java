package org.csu.knowledge.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeListVO {
    /**
     * 知识点唯一标识符
     */
    private Long kpId;

    /**
     * 知识点标题
     */
    private String title;

    /**
     * 知识点内容(省略版)
     */
    private String simpleContent;

    /**
     * 创建者ID
     */
    private Creator creator;

    /**
     * 标签列表 (JSON数组)
     */
    private String tags;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
    /**
     * 状态
     */
    private String status;
    /**
     * 搜索匹配度
     */
    private float searchScore;

    /**
     * 点赞数
     */
    private Integer likeCount;

    @Data
    public static class Creator{
        private Long id;
        private String name;
        private String avatar;
    }
}
