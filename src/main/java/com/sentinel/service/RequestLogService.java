package com.sentinel.service;

import com.sentinel.module.BlockedIp;
import com.sentinel.module.RequestLog;
import com.sentinel.repo.RequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RequestLogService {
    @Autowired
    RequestLogRepository repo;

    @Autowired
    BlockService blockService;

    //***************************METHOD TO SAVE REQUEST LOG IN INTERCEPTOR**************************

    public void requestLog(String ip, String endpoint,
                    String method, String status) {

        RequestLog log = new RequestLog();
        log.setIp(ip);
        log.setEndpoint(endpoint);
        log.setMethod(method);
        log.setStatus(status);
        log.setTime(LocalDateTime.now());

        repo.save(log);
    }

    public long countFailedLoginAttempts(String ip, String endpoint, LocalDateTime since) {
        return repo.countByIpAndEndpointAndStatusAndTimeAfter(ip, endpoint, "FAILED", since);
    }

    //***************************METHODS REQUIRE BY INTERCEPTOR***********************************


    // Shift the rate limit window to after the last block time
    // so previous blocked requests are not counted again
    public long countRecentRequestsByEndpoint(String ip, String endpoint, LocalDateTime since) {
        BlockedIp lastBlock = blockService.findByIp(ip);
        if (lastBlock != null && lastBlock.getBlockedAt() != null) {
            if (lastBlock.getBlockedAt().isAfter(since)) {
                since = lastBlock.getBlockedAt();
            }
        }
        return repo.countByIpAndEndpointAndStatusAndTimeAfter(ip, endpoint, "SUCCESS", since);
    }





}
