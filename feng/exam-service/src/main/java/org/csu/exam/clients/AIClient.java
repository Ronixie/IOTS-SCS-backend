package org.csu.exam.clients;

import org.csu.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

@FeignClient(value = "ai-service",configuration = FeignClientConfig.class)
public interface AIClient {
    @GetMapping("/ai/score")
    String aiToScore(@RequestParam("prompt") String prompt);

    @GetMapping(value="/ai/analysis", produces = "text/plain")
    Flux<String> analysis(@RequestParam("prompt") String prompt);
}
