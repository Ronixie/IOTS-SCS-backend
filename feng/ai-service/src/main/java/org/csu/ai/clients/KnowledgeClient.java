package org.csu.ai.clients;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

@FeignClient(name = "knowledge-service")
public interface KnowledgeClient {

    @GetMapping(value = "/knowledge/{kpId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    String getKnowledge(@PathVariable("kpId") Long kpId);
    
    /**
     * 响应式方式获取知识库内容
     * @param kpId 知识库ID
     * @return 知识库内容
     */
    default Mono<String> getKnowledgeReactive(@PathVariable("kpId") Long kpId) {
        return Mono.fromCallable(() -> getKnowledge(kpId));
    }
}
