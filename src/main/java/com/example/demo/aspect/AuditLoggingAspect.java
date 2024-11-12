package com.example.demo.aspect;

import java.sql.Timestamp;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuditLoggingAspect {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @After("execution(* com.example.demo.controller..*(..))")
    public void logApiCall(JoinPoint joinPoint) {
        // Retrieve the current RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // Check if requestAttributes is null
        if (requestAttributes == null) {
            // No request bound to the current thread, skip logging
            return;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // Retrieve API endpoint and IP address
        String apiEndPoint = request.getRequestURI();
        String ipAddress = request.getRemoteAddr();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Insert log entry into the audit table
        String sql = "INSERT INTO audit (timestamp, api_end_point, ip_address) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, timestamp, apiEndPoint, ipAddress);
    }
}
