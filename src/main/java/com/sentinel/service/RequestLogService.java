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