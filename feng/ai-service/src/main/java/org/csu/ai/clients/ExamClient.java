package org.csu.ai.clients;

import org.csu.config.FeignClientConfig;
import org.csu.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "exam-service", configuration = FeignClientConfig.class)
public interface ExamClient {
    @GetMapping("/exams/{id}/{questionId}")
    Result<Object> getQuestion(@PathVariable("id") String id, @PathVariable("questionId") String questionId);
}
