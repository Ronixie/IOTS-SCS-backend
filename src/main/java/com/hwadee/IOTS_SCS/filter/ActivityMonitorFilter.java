package com.hwadee.IOTS_SCS.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
public class ActivtyMonitorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        filterChain.doFilter(request, response);
    }
}