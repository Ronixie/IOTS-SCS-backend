package org.csu.learn.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LearningPath {
    private String id;
    private String title;
    private String description;
    private int totalDuration; // 总学习时长（小时）
    private Difficulty difficulty;
    private int progress; // 完成进度百分比
    private List<LearningStage> stages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 难度枚举
    enum Difficulty {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}
