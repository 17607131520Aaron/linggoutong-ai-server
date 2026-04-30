package com.linggoutong.server.module.log.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linggoutong.server.common.annotation.LogOperation;
import com.linggoutong.server.common.security.LoginUser;
import com.linggoutong.server.common.security.SecurityUtils;
import com.linggoutong.server.module.log.entity.OperationLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class LogAspect {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public LogAspect(@Autowired(required = false) MongoTemplate mongoTemplate,
                     ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    @Pointcut("@annotation(com.linggoutong.server.common.annotation.LogOperation)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        OperationLog operationLog = new OperationLog();

        try {
            // 获取用户信息
            if (SecurityUtils.isAuthenticated()) {
                LoginUser loginUser = SecurityUtils.getCurrentUser();
                operationLog.setUserId(loginUser.getId());
                operationLog.setUsername(loginUser.getUsername());
            }
        } catch (Exception e) {
            // 未登录状态
        }

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            operationLog.setUrl(request.getRequestURI());
            operationLog.setMethod(request.getMethod());
            operationLog.setIp(getClientIp(request));
        }

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogOperation logAnnotation = signature.getMethod().getAnnotation(LogOperation.class);
        if (logAnnotation != null) {
            operationLog.setModule(logAnnotation.module());
            operationLog.setOperation(logAnnotation.value());
        }

        // 获取请求参数
        try {
            Object[] args = joinPoint.getArgs();
            String params = objectMapper.writeValueAsString(args);
            operationLog.setParams(params.length() > 1000 ? params.substring(0, 1000) : params);
        } catch (Exception e) {
            // 忽略序列化异常
        }

        Object result = null;
        try {
            result = joinPoint.proceed();
            operationLog.setStatus(1);
        } catch (Throwable e) {
            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            operationLog.setExecutionTime(executionTime);
            operationLog.setCreateTime(LocalDateTime.now());

            // 异步保存日志到MongoDB
            if (mongoTemplate != null) {
                try {
                    mongoTemplate.save(operationLog);
                } catch (Exception e) {
                    log.error("保存操作日志失败: {}", e.getMessage());
                }
            }
        }

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.split(",")[0].trim() : "";
    }
}
