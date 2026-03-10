package com.logistics.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.entity.OperationLog;
import com.logistics.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final LogService logService;
    private final ObjectMapper objectMapper;

    @Around("execution(* com.logistics.controller.*.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();

        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return result;

            HttpServletRequest request = attributes.getRequest();
            String method = request.getMethod();
            if ("GET".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
                return result;
            }

            OperationLog opLog = new OperationLog();
            Long userId = (Long) request.getAttribute("userId");
            String username = (String) request.getAttribute("username");
            opLog.setUserId(userId);
            opLog.setUsername(username);
            opLog.setOperation(request.getRequestURI());
            opLog.setMethod(point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName());

            Object[] args = point.getArgs();
            try {
                String params = objectMapper.writeValueAsString(
                        args.length > 0 && !(args[0] instanceof HttpServletRequest) ? args[0] : "");
                if (params.length() > 2000) {
                    params = params.substring(0, 2000);
                }
                opLog.setParams(params);
            } catch (Exception ignored) {
                opLog.setParams("");
            }

            opLog.setIp(request.getRemoteAddr());
            opLog.setOperateTime(LocalDateTime.now());
            //logService.recordOperation(opLog);
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }

        return result;
    }
}
