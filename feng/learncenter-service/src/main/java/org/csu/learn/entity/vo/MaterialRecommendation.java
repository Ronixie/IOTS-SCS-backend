package org.csu.learn.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class MaterialRecommendation {
    private String id;
    private String title;
    private MaterialType type;
    private String author;
    private String url;
    private int readingTime; // 阅读/观看时间（分钟）
    private double rating;
    private List<String> tags;
    private String reason;
    private int matchScore;

    // 资料类型枚举
    public enum MaterialType {
        ARTICLE, VIDEO, BOOK, TUTORIAL, DOCUMENTATION
    }
}
