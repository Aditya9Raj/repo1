package com.example.demo.aspect;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLoggingAspect {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HttpServletRequest request;

    @After("execution(* com.example.demo.controller..*(..))")
    public void logApiCall(JoinPoint joinPoint) {
        // Get API endpoint and IP address
        String apiEndPoint = request.getRequestURI();
        String ipAddress = request.getRemoteAddr();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Insert log entry into the audit table
        String sql = "INSERT INTO audit (timestamp, api_end_point, ip_address) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, timestamp, apiEndPoint, ipAddress);
    }
}
