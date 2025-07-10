package org.csu.learn.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class CourseRecommendation {
    private int courseId;
    private String courseName;
    private String instructor;
    private double rating;
    private LearningPath.Difficulty difficulty;
    private int duration; // 课程时长（小时）
    private int enrolledCount;
    private List<String> tags;
    private String reason; // 推荐理由
    private int matchScore; // 匹配度分数 0-100
    private String thumbnail;
}
