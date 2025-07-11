package com.hwadee.IOTS_SCS.filter;

import com.hwadee.IOTS_SCS.entity.POJO.ApiAccessLog;
import com.hwadee.IOTS_SCS.mapper.LogMapper;
import com.hwadee.IOTS_SCS.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: ActivtyMonitorFilter
* @Package: com.hwadee.IOTS_SCS.filter
* @Description: 监测用户活动并记录
* @author qiershi
* @date 2025/7/2 10:16
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
public class ActivityMonitorFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final LogMapper logMapper;

    public ActivityMonitorFilter(JwtUtil jwtUtil, LogMapper logMapper) {
        this.jwtUtil = jwtUtil;
        this.logMapper = logMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.matches("/users/.+/avatar")) {
            filterChain.doFilter(request, response);
            return;
        }


        long start = System.currentTimeMillis();

        String method = request.getMethod();
        String query = request.getQueryString();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String token = request.getHeader("Authorization");
        String uid = "1";

        if(token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            if(jwtUtil.validateToken(jwt)) {
                uid = jwtUtil.getUidFromToken(jwt);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            int status = response.getStatus();
            long duration = System.currentTimeMillis() - start;

            ApiAccessLog log = new ApiAccessLog();
            log.setUserId(Long.parseLong(uid));
            log.setIp(ip);
            log.setUri(uri);
            log.setMethod(method);
            log.setQuery(query);
            log.setDurationMs((int)duration);
            log.setStatusCode(status);
            log.setUserAgent(userAgent);
            log.setCreatedAt(LocalDateTime.now());
            logMapper.insert(log);
        }


    }
}