package org.csu.learn.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class LearnAnalysisVO {
    /**
     * 学生信息
     */
    private UserVO user;

    /**
     * 课程学习结果
     */
    private List<CourseLearnResult> courseLearnResults;

    /**
     * 发布的知识库的信息
     */
    private List<KnowledgeAnalysis> knowledgeAnalysisList;

    /**
     * AI分析建议
     */
    List<String> aiAnalysisList;
    
    @Data
    public static class CourseLearnResult{
        /**
         * 课程id
         */
        private Long courseId;
        /**
         * 课程名称
         */
        private String courseName;
        /**
         * 考试信息
         */
        private List<ExamAnalysisVO> examAnalysisList;
        /**
         * 作业信息
         */
        private List<AssignmentAnalysisVO> assignmentAnalysisList;
    }
}
