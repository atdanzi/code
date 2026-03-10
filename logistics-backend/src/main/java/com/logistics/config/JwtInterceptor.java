package com.logistics.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.common.Result;
import com.logistics.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        if (uri.startsWith("/api/auth/")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, "未提供有效的认证令牌");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            if (jwtUtil.isTokenExpired(token)) {
                sendError(response, "令牌已过期");
                return false;
            }
            Long userId = jwtUtil.getUserId(token);
            String username = jwtUtil.getUsername(token);
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            return true;
        } catch (Exception e) {
            sendError(response, "无效的认证令牌");
            return false;
        }
    }

    private void sendError(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
