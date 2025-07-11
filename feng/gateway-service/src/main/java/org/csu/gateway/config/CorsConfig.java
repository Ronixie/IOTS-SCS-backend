package org.csu.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource; // 正确导入

@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 允许所有域名进行跨域调用
        config.addAllowedHeader("*"); // 允许任何请求头
        config.addAllowedMethod("*"); // 允许任何方法（POST、GET等）
        config.setAllowCredentials(true); // 允许携带凭证（如Cookie）

        // 使用 WebFlux 的配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config); // 对所有接口都有效

        return new CorsWebFilter(source);
    }
}
