package org.csu.ai.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.RequiredArgsConstructor;
import org.csu.ai.memory.MongoDBChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class AIConfig {

    @Value("${csu.ai.api-key}")
    private String apiKey;
    @Value("${csu.ai.model}")
    private String model;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
    }

    @Bean
    public ChatClient dashScopeChatClient(MongoDBChatMemory memory) {
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(apiKey).build();
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .withModel(model)
                .withMultiModel(true)
                .withStream(true)
                .withTemperature(0.8)
                .build();
        DashScopeChatModel model = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(options)
                .build();
        return ChatClient.builder(model)
                .defaultSystem("你是一个学习机器人，帮助学生解疑答惑，所有的回答均以MarkDown的格式输出，每次回答之前不要介绍自己")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build()
                )
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                //.defaultTools(tools)
                .build();
    }

    @Bean
    @Primary
    public ChatModel dsChatModel() {
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(apiKey).build();
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .withMultiModel(true)
                .withStream(true)
                .withTemperature(0.8)
                .withModel(DashScopeApi.ChatModel.QWEN_PLUS.getModel())
                .build();
        return DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(options)
                .build();
    }

}
