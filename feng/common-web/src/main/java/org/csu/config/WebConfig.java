package org.csu.config;

import org.csu.Interceptor.UserIdInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类，用于配置Spring MVC相关设置
 */
@Configuration
@ConditionalOnClass(DispatcherServlet.class)
@Order(Ordered.HIGHEST_PRECEDENCE) // 确保优先级最高
public class WebConfig implements WebMvcConfigurer {
    /**
     * 创建并返回用户ID拦截器实例
     * @return UserIdInterceptor 用户ID拦截器实例
     */
    @Bean
    public UserIdInterceptor userIdInterceptor() {
        return new UserIdInterceptor();
    }

    /**
     * 添加拦截器配置
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有请求
        registry.addInterceptor(userIdInterceptor())
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有接口
                .allowedOriginPatterns("*") // 允许所有域名
                .allowCredentials(true) // 允许携带凭证
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                .allowedHeaders("*") // 允许所有请求头
                .exposedHeaders("*") // 允许所有响应头
                .maxAge(3600); // 预检请求的有效期
    }
}