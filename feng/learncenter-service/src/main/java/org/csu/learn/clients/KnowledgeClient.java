package org.csu.learn.clients;


import org.csu.config.FeignClientConfig;
import org.csu.learn.entity.vo.KnowledgeAnalysis;
import org.csu.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "knowledge-service",configuration = FeignClientConfig.class)
public interface KnowledgeClient {
    @GetMapping("/knowledge/analysis")
    Result<List<KnowledgeAnalysis>> getAnalysis();
}

