package com.sentinel.dto;

import com.sentinel.module.BlockedIp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class BlockedPageStats {
    private String ip;
    private LocalDateTime blockedTill;
    private String status;

    public BlockedPageStats(BlockedIp newBlock) {
        this.ip = newBlock.getIp();
        this.blockedTill = newBlock.getBlockedTill();
        this.status = newBlock.getBlockedTill().isAfter(LocalDateTime.now(ZoneOffset.UTC))
                ? "ACTIVE" : "EXPIRED";
    }

    public String getIp() { return ip; }
    public LocalDateTime getBlockedTill() { return blockedTill; }
    public String getStatus() { return status; }
}