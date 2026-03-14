package com.sentinel.controller;

import com.sentinel.dto.BlockedPageStats;
import com.sentinel.dto.DashboardStatsDTO;
import com.sentinel.dto.RequestPerMinDto;
import com.sentinel.module.BlockedIp;
import com.sentinel.module.IpLocation;
import com.sentinel.module.RequestLog;
import com.sentinel.module.SecurityAlert;
import com.sentinel.repo.BlockedIPRepository;
import com.sentinel.repo.IpLocationRepository;
import com.sentinel.repo.RequestLogRepository;
import com.sentinel.repo.SecurityAlertRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final RequestLogRepository requestLogRepo;
    private final SecurityAlertRepository alertRepo;
    private final BlockedIPRepository blockedRepo;
    private final IpLocationRepository ipLocationRepo;

    // Constructor injection
    public AdminController(RequestLogRepository requestLogRepo,
                           BlockedIPRepository blockedRepo,
                           SecurityAlertRepository securityAlertRepository,
                           IpLocationRepository ipLocationRepo) {
        this.requestLogRepo = requestLogRepo;
        this.blockedRepo    = blockedRepo;
        this.alertRepo      = securityAlertRepository;
        this.ipLocationRepo = ipLocationRepo;
    }

    @GetMapping("/stats")
    public DashboardStatsDTO getStats(){

        long totalRequest=requestLogRepo.count();
        long failedRequest=requestLogRepo.countByStatus("FAILED");
        long activeAlert=alertRepo.count();
        long blockedIp=blockedRepo.countActiveBlocked();

        return new DashboardStatsDTO(totalRequest,failedRequest,activeAlert,blockedIp);
    }

    @GetMapping("/requests-per-minute")
    public List<RequestPerMinDto> getRPM(){

        List<RequestPerMinDto> requestPerMinDtos = new ArrayList<>();
        List<Object[]> results = requestLogRepo.getLast10MinutesData();

        for (Object[] row : results) {
            String minute = (String) row[0];
            Long count    = ((Number) row[1]).longValue();
            requestPerMinDtos.add(new RequestPerMinDto(minute, count));
        }

        return requestPerMinDtos;
    }

    @GetMapping("/recent-blocked")
    public List<BlockedIp> getRecentBlocked() {
        return blockedRepo.findTop5ByOrderByBlockedTillDesc();
    }

    @GetMapping("/blocked-ips")
    public List<BlockedPageStats> getBlockedIps() {
        List<BlockedIp> blockedList = blockedRepo.findAllByOrderByBlockedTillDesc();
        List<BlockedPageStats> result = new ArrayList<>();
        for (BlockedIp b : blockedList) {
            result.add(new BlockedPageStats(b));
        }
        return result;
    }

    @PostMapping("/unblock/{ip}")
    public String unBlockIP(@PathVariable String ip){

        BlockedIp block = blockedRepo.findTopByIpOrderByBlockedTillDesc(ip);
        if (block != null) {
            block.setBlockedTill(LocalDateTime.now().minusMinutes(1));
            blockedRepo.save(block);
            return "UNBLOCKED";
        }
        return "IP NOT FOUND";
    }

    @GetMapping("/requests")
    public List<RequestLog> getRequests() {
        return requestLogRepo.findAllByOrderByTimeDesc();
    }

    @GetMapping("/alerts")
    public List<SecurityAlert> getAlerts() {
        return alertRepo.findAllByOrderByTimeDesc();
    }

    // Geo alerts — combines alert data with IP location for map
    @GetMapping("/geo-alerts")
    public List<Map<String, Object>> getGeoAlerts() {
        List<SecurityAlert> alerts = alertRepo.findAllByOrderByTimeDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        for (SecurityAlert alert : alerts) {
            // Get lat/lng from ip_location table
            IpLocation location = ipLocationRepo.findById(alert.getIp()).orElse(null);

            Map<String, Object> row = new HashMap<>();
            row.put("ip",         alert.getIp());
            row.put("attackType", alert.getAttackType());
            row.put("severity",   alert.getSeverity());
            row.put("country",    alert.getCountry());
            row.put("city",       alert.getCity());
            row.put("isp",        alert.getIsp());
            row.put("lat",        location != null ? location.getLat() : null);
            row.put("lng",        location != null ? location.getLng() : null);
            result.add(row);
        }
        return result;
    }

    // Top endpoints by request count
    @GetMapping("/top-endpoints")
    public List<Map<String, Object>> getTopEndpoints() {
        List<Object[]> results = requestLogRepo.getTopEndpoints();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("name",  row[0]);
            map.put("count", ((Number) row[1]).longValue());
            result.add(map);
        }
        return result;
    }
}

