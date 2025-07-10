package org.csu.learn.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.learn.clients.*;
import org.csu.learn.entity.vo.*;
import org.csu.learn.service.LearnCenterService;
import org.csu.utils.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearnCenterServiceImpl implements LearnCenterService {
    private final OtherClient otherClient;
    private final ExamClient examClient;
    private final AssignmentClient assignmentClient;
    private final KnowledgeClient knowledgeClient;
    private final AIClient aiClient;

    @Override
    public LearnAnalysisVO getLearnAnalysis(Long studentId) {
        LearnAnalysisVO vo = new LearnAnalysisVO();
        // 获取用户信息
        UserVO user = _getUserInfo(studentId);
        vo.setUser(user);
        // 获取课程信息
        List<Long> courseIds = otherClient.getCourseIdsByUid(studentId).getData();
        List<LearnAnalysisVO.CourseLearnResult> results = new ArrayList<>();
        courseIds.forEach(courseId -> {
            LearnAnalysisVO.CourseLearnResult result = new LearnAnalysisVO.CourseLearnResult();
            // 获取课程名称
            String courseName = otherClient.getCourseName(courseId).getData();
            // 获取考试信息
            List<ExamAnalysisVO> examAnalysis = _getExamAnalysis(courseId);
            // 获取作业信息
            List<AssignmentAnalysisVO> assignmentAnalysis = _getAssignmentAnalysis(courseId);
            result.setCourseId(courseId);
            result.setCourseName(courseName);
            result.setExamAnalysisList(examAnalysis);
            result.setAssignmentAnalysisList(assignmentAnalysis);
            results.add(result);
        });
        vo.setCourseLearnResults(results);
        // 获取知识库信息
        List<KnowledgeAnalysis> knowledgeAnalysisList = _getKnowledgeAnalysis();
        vo.setKnowledgeAnalysisList(knowledgeAnalysisList);

        // 获取AI建议
        //vo.setAiAnalysisList(null);
        /*String prompt = JSON.toJSONString(vo);
        String aiAnalysis = _aiAnalysis(prompt);
        vo = JSON.parseObject(aiAnalysis, LearnAnalysisVO.class);*/
        return vo;
    }

    /*@Override
    public List<String> getAiAnalysis(Long studentId) {
        // 复用分析数据
        LearnAnalysisVO vo = getLearnAnalysis(studentId);
        String prompt = JSON.toJSONString(vo);
        String aiAnalysis = _aiAnalysis(prompt);
        Pattern pattern = Pattern.compile("```json\\n(.*?)\\n```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(aiAnalysis);
        if (matcher.find()) { // 判断是否匹配
            // group(1) 获取第一个分组（即 ```json 和 ``` 之间的内容）
            String jsonContent = matcher.group(1).trim(); // trim() 去除首尾空格
            LearnAnalysisVO aiVO = JSON.parseObject(jsonContent, LearnAnalysisVO.class);
            return aiVO.getAiAnalysisList();
        }else{
            return new ArrayList<>();
        }
    }*/
    public String getAiAnalysis(Long studentId) {
        // 复用分析数据
        LearnAnalysisVO vo = getLearnAnalysis(studentId);
        String prompt = JSON.toJSONString(vo);
        return _aiAnalysis(prompt);
    }

    @Override
    public LearningPath getPath(long studentId) {
        String prompt= """
                请生成一个 JSON 对象：
               
                 一个表示 LearningPath 的 JSON，包含以下所有字段：
                   - id: 字符串类型的唯一标识符（例如 "lp-12345"）
                   - title: 学习路径的标题（字符串）
                   - description: 详细描述（字符串）
                   - totalDuration: 总学习时长（数字，单位为小时）
                   - difficulty: 难度级别（字符串，可选值为 "beginner", "intermediate", "advanced"）
                   - progress: 完成进度百分比（数字，0-100）
                   - stages: 学习阶段数组（至少包含2个元素）
                     每个阶段包含：
                     - id: 阶段唯一标识符（字符串）
                     - title: 阶段标题（字符串）
                     - description: 阶段描述（字符串）
                     - duration: 预计学习时长（数字，小时）
                     - status: 阶段状态（字符串，可选值为 "not_started", "in_progress", "completed"）
                     - progress: 阶段进度百分比（数字，0-100）
                     - courses: 课程数组（至少包含1个required和1个optional课程）
                       每门课程包含：
                       - courseId: 课程ID（数字）
                       - courseName: 课程名称（字符串）
                       - type: 课程类型（字符串，可选值为 "required", "optional"）
                       - estimatedHours: 预计学习小时数（数字）
                       - completed: 是否完成（布尔值）
                     - prerequisites: 前置阶段ID数组（字符串数组）
                     - skills: 学习后掌握的技能数组（字符串数组）
                   - createdAt: 创建时间（ISO 8601格式字符串，如 "2023-01-01T12:00:00Z"）
                   - updatedAt: 更新时间（ISO 8601格式字符串）
                
                请确保生成的 JSON 严格遵循上述结构，包含所有字段，并且字段值符合指定的类型和约束。
                """;
        String s = aiClient.generate(prompt);
        Pattern pattern = Pattern.compile("```json\\n(.*?)\\n```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) { // 判断是否匹配
            // group(1) 获取第一个分组（即 ```json 和 ``` 之间的内容）
            String jsonContent = matcher.group(1).trim(); // trim() 去除首尾空格
            return JSON.parseObject(jsonContent, LearningPath.class);
        }
        return null;
    }

    @Override
    public RecommendationData getCourseRecommendation(long studentId) {
        String prompt= """
                请生成一个 JSON 对象：
                
                  一个表示 RecommendationData 的 JSON，包含以下所有字段：
                   - courses: 推荐课程数组（至少包含3个课程）
                     每门课程包含：
                     - courseId: 课程ID（数字）
                     - courseName: 课程名称（字符串）
                     - instructor: 讲师名称（字符串）
                     - rating: 评分（数字，0-5）
                     - difficulty: 难度级别（字符串，可选值同LearningPath）
                     - duration: 课程时长（数字，小时）
                     - enrolledCount: 已报名人数（数字）
                     - tags: 标签数组（字符串数组）
                     - reason: 推荐理由（字符串）
                     - matchScore: 匹配度分数（数字，0-100）
                     - thumbnail: 缩略图URL（字符串）
                   - experiments: 推荐实验数组（至少包含2个实验）
                     每个实验包含：
                     - id: 实验ID（字符串）
                     - title: 实验标题（字符串）
                     - description: 实验描述（字符串）
                     - difficulty: 难度级别（字符串）
                     - estimatedTime: 预计完成时间（数字，小时）
                     - category: 实验类别（字符串）
                     - tools: 需要的工具/技术数组（字符串数组）
                     - reason: 推荐理由（字符串）
                     - matchScore: 匹配度分数（数字）
                   - materials: 推荐资料数组（至少包含3种不同类型的资料）
                     每种资料包含：
                     - id: 资料ID（字符串）
                     - title: 资料标题（字符串）
                     - type: 资料类型（字符串，可选值为 "article", "video", "book", "tutorial", "documentation"）
                     - author: 作者/来源（字符串）
                     - url: 资料URL（字符串）
                     - readingTime: 阅读/观看时间（数字，分钟）
                     - rating: 评分（数字，0-5）
                     - tags: 标签数组（字符串数组）
                     - reason: 推荐理由（字符串）
                     - matchScore: 匹配度分数（数字）
                   - lastUpdated: 最后更新时间（ISO 8601格式字符串）
                
                请确保生成的 JSON 严格遵循上述结构，包含所有字段，并且字段值符合指定的类型和约束。
                """;
        String s = aiClient.generate(prompt);
        Pattern pattern = Pattern.compile("```json\\n(.*?)\\n```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) { // 判断是否匹配
            // group(1) 获取第一个分组（即 ```json 和 ``` 之间的内容）
            String jsonContent = matcher.group(1).trim(); // trim() 去除首尾空格
            return JSON.parseObject(jsonContent, RecommendationData.class);
        }
        return null;
    }

    private List<KnowledgeAnalysis> _getKnowledgeAnalysis() {
        return knowledgeClient.getAnalysis().getData();
    }

    private UserVO _getUserInfo(Long studentId) {
        return otherClient.getUserInfo(studentId).getData();
    }

    private List<ExamAnalysisVO> _getExamAnalysis(Long courseId) {
        return examClient.forAnalysis(courseId).getData();
    }

    private List<AssignmentAnalysisVO> _getAssignmentAnalysis(Long courseId) {
        return assignmentClient.getAssignmentAnalysis(courseId).getData();
    }

    private String _aiAnalysis(String prompt) {
        return aiClient.analysisStudent(prompt);
    }
}
