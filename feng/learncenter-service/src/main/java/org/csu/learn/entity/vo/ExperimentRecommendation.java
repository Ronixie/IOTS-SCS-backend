package org.csu.learn.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class ExperimentRecommendation {
    private String id;
    private String title;
    private String description;
    private LearningPath.Difficulty difficulty;
    private int estimatedTime; // 预计完成时间（小时）
    private String category;
    private List<String> tools; // 需要的工具/技术
    private String reason;
    private int matchScore;
}
