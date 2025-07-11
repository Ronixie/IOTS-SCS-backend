package org.csu.learn.clients;

import org.csu.config.FeignClientConfig;
import org.csu.learn.entity.vo.ExamAnalysisVO;
import org.csu.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "exam-service",configuration = FeignClientConfig.class)
public interface ExamClient {
    @GetMapping("/exams/analysis")
    Result<List<ExamAnalysisVO>> forAnalysis(@RequestParam("courseId") Long courseId);
}
