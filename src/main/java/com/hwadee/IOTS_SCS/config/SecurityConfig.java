package com.hwadee.IOTS_SCS.config;

import com.hwadee.IOTS_SCS.filter.ActivityMonitorFilter;
import com.hwadee.IOTS_SCS.filter.AdminCertificationFilter;
import com.hwadee.IOTS_SCS.filter.JwtAuthenticationFilter;
import com.hwadee.IOTS_SCS.mapper.AdminMapper;
import com.hwadee.IOTS_SCS.mapper.LogMapper;
import com.hwadee.IOTS_SCS.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
/**
* @ProjectName: IOTS-SCS-backend
* @Title: SecurutyConfig
* @Package: com.hwadee.IOTS_SCS.config
* @Description: 用于解决用户认证问题和跨域问题
* @author qiershi
* @date 2025/7/1 10:56
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LogMapper logMapper) throws Exception {
        http
                // 启用 CORS
                .cors(cors -> {})
                // 禁用 CSRF
                .csrf(csrf -> csrf.disable())
                // 无状态会话
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/captcha").permitAll() // 公开访问
                        .requestMatchers("/api/users/{uid}/avatar").permitAll()
                        .anyRequest().authenticated() // 其他请求需认证
                )
                // 注册过滤器
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new AdminCertificationFilter(jwtUtil, adminMapper), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ActivityMonitorFilter(jwtUtil, logMapper), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // 允许所有来源
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}