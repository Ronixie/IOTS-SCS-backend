package org.csu.learn.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class LearningStage {
    private String id;
    private String title;
    private String description;
    private int duration; // 预计学习时长（小时）
    private StageStatus status;
    private int progress;
    private List<StageCourse> courses;
    private List<String> prerequisites; // 前置阶段ID
    private List<String> skills; // 将要掌握的技能

    // 阶段状态枚举
    public enum StageStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
}

