package com.sentinel.interceptor;

import com.sentinel.module.BlockedIp;
import com.sentinel.service.AlertService;
import com.sentinel.service.BlockService;
import com.sentinel.service.RequestLogService;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class ApiInterceptor implements HandlerInterceptor {

    @Autowired
    RequestLogService logService;

    @Autowired
    BlockService blockService;

    @Autowired
    AlertService alertService;

    private final int LIMIT = 5;
    private final String HONEYPOT_1 = "/api/answer-key/download";
    private final String HONEYPOT_2 = "/api/exam/question-paper";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        String ip = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        LocalDateTime now = LocalDateTime.now();

        // STEP 1 : HONEYPOT CHECK
        if (endpoint.equals(HONEYPOT_1) || endpoint.equals(HONEYPOT_2)) {
            BlockedIp block = new BlockedIp();
            block.setIp(ip);
            block.setBlockedAt(now);
            block.setBlockedTill(now.plusMinutes(30));
            blockService.blockIp(block);

            alertService.createAlert(ip, endpoint, "HONEYPOT", "CRITICAL", now);
            logService.requestLog(ip, endpoint, request.getMethod(), "FAILED");

            response.getWriter().write("CRITICAL ALERT: Attacker detected");
            return false;
        }

        // STEP 2 : BLOCKED IP CHECK
        BlockedIp blocked = blockService.findByIp(ip);
        if (blocked != null && blocked.getBlockedTill().isAfter(now)) {
            response.setStatus(403);
            response.getWriter().write("Your IP is BLOCKED");
            logService.requestLog(ip, endpoint, request.getMethod(), "FAILED");
            return false;
        }

        // STEP 3 : BRUTE FORCE CHECK
        if (endpoint.equals("/api/auth/login")) {
            long failedCount = logService.countFailedLoginAttempts(ip, "/api/auth/login", now.minusMinutes(10));
            if (failedCount >= 5) {
                BlockedIp newBlock = new BlockedIp();
                newBlock.setIp(ip);
                newBlock.setBlockedAt(now);
                newBlock.setBlockedTill(now.plusMinutes(30));
                blockService.blockIp(newBlock);

                alertService.createAlert(ip, endpoint, "BRUTE FORCE", "HIGH", now);
                logService.requestLog(ip, endpoint, request.getMethod(), "FAILED");

                response.getWriter().write("You have violated our guidelines. Your IP has been blocked.");
                return false;
            }
            return true;
        }

        // STEP 4 : RATE LIMIT CHECK
        long count = logService.countRecentRequestsByEndpoint(ip, endpoint, now.minusMinutes(1));
        if (count >= LIMIT) {
            BlockedIp newBlock = new BlockedIp();
            newBlock.setIp(ip);
            newBlock.setBlockedAt(now);
            newBlock.setBlockedTill(now.plusMinutes(10));
            blockService.blockIp(newBlock);

            alertService.createAlert(ip, endpoint, "RATE LIMIT ATTACK", "MEDIUM", now);
            logService.requestLog(ip, endpoint, request.getMethod(), "FAILED");

            response.getWriter().write("Too many requests. IP Blocked");
            return false;
        }

        // STEP 5 : NORMAL REQUEST SAVE
        if (!endpoint.equals("/api/auth/login")) {
            logService.requestLog(ip, endpoint, request.getMethod(), "SUCCESS");
        }
        return true;
    }
}