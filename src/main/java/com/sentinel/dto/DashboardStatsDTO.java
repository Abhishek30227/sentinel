package com.sentinel.dto;

public class DashboardStatsDTO {

    private long totalRequests;   // request_log table count
    private long failedRequests;  // request_log WHERE status = 'FAILED'
    private long activeAlerts;    // security_alert table count
    private long blockedIps;      // blocked_ip WHERE blockedTill > NOW()

    // Constructor
    public DashboardStatsDTO(long totalRequests, long failedRequests,
                             long activeAlerts, long blockedIps) {
        this.totalRequests  = totalRequests;
        this.failedRequests = failedRequests;
        this.activeAlerts   = activeAlerts;
        this.blockedIps     = blockedIps;
    }

    // Getters
    public long getTotalRequests()  { return totalRequests; }
    public long getFailedRequests() { return failedRequests; }
    public long getActiveAlerts()   { return activeAlerts; }
    public long getBlockedIps()     { return blockedIps; }
}
