package org.csu.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.csu.utils.UserContext;
import org.springframework.context.annotation.Bean;

/**
 * Feign客户端配置类
 * 配置Feign客户端的日志级别和请求拦截器
 */
public class FeignClientConfig {

    /**
     * 配置Feign客户端的日志级别
     * @return 返回BASIC级别的日志
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * 配置Feign客户端的请求拦截器
     * @return 返回添加用户ID请求头的拦截器
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            long userId = UserContext.getUser();
            if (userId != 0) {
                requestTemplate.header("X-User-Id", String.valueOf(userId));
            }
        };
    }
}