package org.csu.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.csu.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 公共拦截器，获取用户id并储存
 */
public class UserIdInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头提取userid
        String s = request.getHeader("X-User-Id");
        long userId = 0L;
        if (s != null) {
            userId=Long.parseLong(s);
        }
        // 存入ThreadLocal上下文，方便后续业务代码获取
        UserContext.setUser(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 从ThreadLocal上下文中移除用户ID
        UserContext.removeUser();
    }
}
