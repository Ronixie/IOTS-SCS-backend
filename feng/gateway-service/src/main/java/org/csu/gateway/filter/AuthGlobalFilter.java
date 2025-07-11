package org.csu.gateway.filter;


import lombok.RequiredArgsConstructor;
import org.csu.exception.UnauthorizedException;
import org.csu.gateway.config.AuthProperties;
import org.csu.utils.JwtUtil;
import org.csu.utils.TokenTools;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 拦截器
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final AuthProperties authProperties;
    private final TokenTools tokenTools;
    //路径匹配
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final JwtUtil jwtUtil;

    /**
     * 拦截器主要逻辑代码
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> authorization = request.getHeaders().get(authProperties.getFrontShortTokenName());
        String token;
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.get(0).split(" ")[1];
            //token = authorization.get(0);
        } else {
            token = null;
        }
        String userId;
        try {
            userId = String.valueOf(tokenTools.parseShortToken(token));
/*            if (token != null) {
                userId = jwtUtil.getUidFromToken(token);
            }*/
        } catch (UnauthorizedException e) {
            if (isExclude(request.getPath().toString())) {
                return chain.filter(exchange);
            }
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }
        // 将用户id放到请求头中
        ServerWebExchange swe = exchange.mutate().
                request(builder -> {
                    builder.header("X-User-Id", userId);
                    if (isExclude(request.getPath().toString()))
                        builder.header("Authorization", "Bearer "+token);
                })
                .build();
        return chain.filter(swe);
    }

    /**
     * 判断优先级
     *
     * @return order
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 判断是否排除该路径
     *
     * @param antPath 路由
     * @return true or false
     */
    private boolean isExclude(String antPath) {
        for (String pathPattern : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathPattern, antPath)) {
                return true;
            }
        }
        return false;
    }
}
