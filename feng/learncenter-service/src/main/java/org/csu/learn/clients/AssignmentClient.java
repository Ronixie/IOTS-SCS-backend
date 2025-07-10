package org.csu.learn.clients;

import org.csu.config.FeignClientConfig;
import org.csu.learn.entity.vo.AssignmentAnalysisVO;
import org.csu.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "homework-service",configuration = FeignClientConfig.class)
public interface AssignmentClient {
    @GetMapping("/assignments/analysis")
    Result<List<AssignmentAnalysisVO>> getAssignmentAnalysis(@RequestParam("courseId") Long courseId);
}
