package org.csu.knowledge.entity.dto;


import lombok.Data;

@Data
public class KnowledgeDTO {
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
     * 附件列表 (JSON数组)
     */
    private String attachmentsJson;

    /**
     * 标签列表 (JSON数组)
     */
    private String tags;
}
