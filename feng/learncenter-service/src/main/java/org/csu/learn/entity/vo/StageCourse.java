package org.csu.learn.entity.vo;

import lombok.Data;

@Data
public class StageCourse {
    private int courseId;
    private String courseName;
    private CourseType type;
    private int estimatedHours;
    private boolean completed;

    // 课程类型枚举
    public enum CourseType {
        REQUIRED, OPTIONAL
    }
}
