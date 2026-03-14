package com.sentinel.interceptor;

import com.sentinel.module.BlockedIp;
import com.sentinel.module.RequestLog;

import com.sentinel.module.SecurityAlert;
import com.sentinel.repo.BlockedIPRepository;
import com.sentinel.repo.RequestLogRepository;
import com.sentinel.repo.SecurityAlertRepository;
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

        // STEP 1:Generate a current ip

        String ip = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        LocalDateTime now = LocalDateTime.now();

        // =====================================================
        // STEP 1 : HONEYPOT CHECK
        // =====================================================

        String endPoint = request.getRequestURI();
        if (endpoint.equals(HONEYPOT_1) || endpoint.equals(HONEYPOT_2)){
            BlockedIp block = new BlockedIp();
            block.setIp(ip);
            block.setBlockedTill(LocalDateTime.now().plusMinutes(30));

            blockService.blockIp(block);

            response.getWriter().write("CRITICAL ALERT: Attacker detected");


            // table for security alert

           alertService.createAlert(ip,endPoint,"HONEYPOT","CRITICAL",now);

            // Save Request Log
           logService.requestLog(ip,endPoint,request.getMethod(),"FAILED");

            return false;
        }

        // =====================================================
        // STEP 2 : BLOCKED IP CHECK
        // =====================================================

        BlockedIp blocked = blockService.findByIp(ip);
        if (blocked != null) {
            if (blocked.getBlockedTill().isAfter(now)) {
                response.setStatus(403);
                response.getWriter().write("Your IP is BLOCKED");

                // Save Request Log
                logService.requestLog(ip,endPoint,request.getMethod(),"FAILED");

                return false;
            }
        }

        // =====================================================
        // STEP 3 : Wrong password brute force check
        // =====================================================
        if (endpoint.equals("/api/auth/login")) {
            LocalDateTime tenMinutesAgo = now.minusMinutes(10);
            long failedCount = logService.countFailedLoginAttempts(ip, "/api/auth/login", tenMinutesAgo);

            if (failedCount >= 5) {
                BlockedIp newBlock = new BlockedIp();
                newBlock.setIp(ip);
                newBlock.setBlockedTill(now.plusMinutes(30));
                blockService.blockIp(newBlock);

                alertService.createAlert(ip, endpoint, "BRUTE FORCE", "HIGH", now);
                logService.requestLog(ip, endpoint, request.getMethod(), "FAILED");

                response.getWriter().write("You have violated our guidelines. Your IP has been blocked.");
                return false;
            }
            return true;
        }

        // =====================================================
        // STEP 4 : RATE LIMIT CHECK
        // =====================================================

        LocalDateTime oneMinuteAgo = now.minusMinutes(1);
        long count = logService.countRecentRequestsByEndpoint(ip, endpoint, now.minusMinutes(1));

        if (count >= LIMIT){
            BlockedIp newBlock = new BlockedIp();
            newBlock.setIp(ip);
            newBlock.setBlockedAt(now);
            newBlock.setBlockedTill(LocalDateTime.now().plusMinutes(10));
            blockService.blockIp(newBlock);
            response.getWriter().write("Too many requests. IP Blocked");


            // table for security alert
            alertService.createAlert(ip,endPoint,"RATE LIMIT ATTACK","MEDIUM",now);


            // Save Request Log
            logService.requestLog(ip,endPoint,request.getMethod(),"FAILED");

            return false;

        }
        // =====================================================
        // STEP 5 : NORMAL REQUEST SAVE
        // =====================================================

        if (!endpoint.equals("/api/auth/login")) {
            logService.requestLog(ip, endpoint, request.getMethod(), "SUCCESS");
        }
        return true;


    }
}
