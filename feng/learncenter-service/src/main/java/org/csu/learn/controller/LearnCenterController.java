package org.csu.learn.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.exception.UnauthorizedException;
import org.csu.learn.entity.vo.CourseRecommendation;
import org.csu.learn.entity.vo.LearnAnalysisVO;
import org.csu.learn.entity.vo.LearningPath;
import org.csu.learn.entity.vo.RecommendationData;
import org.csu.learn.service.LearnCenterService;
import org.csu.utils.Result;
import org.csu.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/learn/analysis")
@Slf4j
public class LearnCenterController {
    private final LearnCenterService learnCenterService;

    // 1. 基础分析数据（不含AI建议）
    @GetMapping
    public Result<LearnAnalysisVO> getLearnAnalysis() {
        long studentId = UserContext.getUser();
        log.info("studentId:{}的学生获取学习分析", studentId);
        if (studentId == 0) {
            throw new UnauthorizedException("未登录");
        }
        LearnAnalysisVO vo = learnCenterService.getLearnAnalysis(studentId);
        return Result.success(vo);
    }

    // 2. AI建议（单独接口）
    @GetMapping("/ai")
    public Result<String> getAiAnalysis() {
        long studentId = UserContext.getUser();
        log.info("studentId:{}的学生获取AI学习建议", studentId);
        if (studentId == 0) {
            throw new UnauthorizedException("未登录");
        }
        return Result.success(learnCenterService.getAiAnalysis(studentId));
    }

    @GetMapping("/path")
    public Result<LearningPath> getPath() {
        long studentId = UserContext.getUser();
        log.info("studentId:{}的学生获取学习路径", studentId);
        if (studentId == 0) {
            throw new UnauthorizedException("未登录");
        }
        return Result.success(learnCenterService.getPath(studentId));
    }

    @GetMapping("/recommend")
    public Result<RecommendationData> getRecommend() {
        long studentId = UserContext.getUser();
        log.info("studentId:{}的学生获取课程推荐", studentId);
        if (studentId == 0) {
            throw new UnauthorizedException("未登录");
        }
        return Result.success(learnCenterService.getCourseRecommendation(studentId));
    }
}
