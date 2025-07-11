package org.csu.learn.clients;

import org.csu.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "ai-service",configuration = FeignClientConfig.class)
public interface AIClient {
    @GetMapping("/ai/analysis/student")
    String analysisStudent(@RequestParam("prompt") String prompt);

    @GetMapping("/ai/generate")
    String generate(@RequestParam("prompt") String prompt);
}
