package org.csu.learn.service;

import org.csu.learn.entity.vo.CourseRecommendation;
import org.csu.learn.entity.vo.LearnAnalysisVO;
import org.csu.learn.entity.vo.LearningPath;
import org.csu.learn.entity.vo.RecommendationData;

import java.util.List;

public interface LearnCenterService {
    LearnAnalysisVO getLearnAnalysis(Long studentId);
    //List<String> getAiAnalysis(Long studentId);
    String getAiAnalysis(Long studentId);

    LearningPath getPath(long studentId);

    RecommendationData getCourseRecommendation(long studentId);
}
