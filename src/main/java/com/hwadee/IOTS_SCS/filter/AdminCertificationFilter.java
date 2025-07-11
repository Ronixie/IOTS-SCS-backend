package com.hwadee.IOTS_SCS.filter;

import com.hwadee.IOTS_SCS.mapper.AdminMapper;
import com.hwadee.IOTS_SCS.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: AdminCertificationFilter
* @Package: com.hwadee.IOTS_SCS.filter
* @Description: 管理员认证过滤器
* @author qiershi
* @date 2025/7/2 8:30
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Component
public class AdminCertificationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final AdminMapper adminMapper;

    public AdminCertificationFilter(JwtUtil jwtUtil, AdminMapper adminMapper) {
        this.jwtUtil = jwtUtil;
        this.adminMapper = adminMapper;
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

        if(request.getRequestURI().contains("admin")) {
            String header = request.getHeader("Authorization");
            String token = header.substring(7);

            String uid = jwtUtil.getUidFromToken(token);

            if (! adminMapper.getRoleByUid(uid).toLowerCase(Locale.ROOT).equals("admin")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\":\"非法访问\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}