package org.csu.learn.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationData {
    private List<CourseRecommendation> courses;
    private List<ExperimentRecommendation> experiments;
    private List<MaterialRecommendation> materials;
    private LocalDateTime lastUpdated;
}
