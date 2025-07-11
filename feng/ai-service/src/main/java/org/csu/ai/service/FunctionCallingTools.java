package org.csu.ai.service;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.ai.clients.KnowledgeClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FunctionCallingTools {
    private final KnowledgeClient knowledgeClient;

    @Tool(name = "GetKnowledgeBaseContent", description = "根据kpId获取某个知识库内容")
    public String getKnowledgeBaseContent(@Parameter(
            name = "kpId",
            description = "查询的知识库的所需的id,不能为空，必须为整型"
    ) Long kpId) {
        log.info("调用GetKnowledgeBaseContent工具，kpId: {}", kpId);
        
        if (kpId == null) {
            log.error("kpId参数不能为空");
            return "错误：知识库ID不能为空";
        }
        
        try {
            // 使用响应式方式调用，但在这里我们需要阻塞等待结果
            // 因为@Tool注解的方法需要返回String而不是Mono<String>
            String result = knowledgeClient.getKnowledgeReactive(kpId).block();
            
            if (!StringUtils.hasText(result)) {
                log.warn("知识库ID {} 未找到内容", kpId);
                return "未找到该知识库的内容";
            }
            log.info("成功获取知识库内容，kpId: {}, 内容长度: {}", kpId, result.length());
            return result;
        } catch (Exception e) {
            log.error("获取知识库内容失败，kpId: {}", kpId, e);
            return "获取知识库内容时发生错误：" + e.getMessage();
        }
    }
}
