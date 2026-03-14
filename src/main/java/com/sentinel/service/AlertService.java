package com.sentinel.service;

import com.sentinel.module.IpLocation;
import com.sentinel.module.SecurityAlert;
import com.sentinel.repo.SecurityAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AlertService {

    @Autowired
    SecurityAlertRepository securityAlertRepository;

    private final SecurityAlertRepository repository;
    private final GeoLocationService geoLocationService;

    public AlertService(SecurityAlertRepository repository,
                                GeoLocationService geoLocationService) {
        this.repository = repository;
        this.geoLocationService = geoLocationService;
    }

    public void createAlertWithLocation(SecurityAlert alert) {
        // IP se location fetch karo
        IpLocation location = geoLocationService.getLocation(alert.getIp());

        if (location != null) {
            alert.setCountry(location.getCountry());
            alert.setCity(location.getCity());
            alert.setIsp(location.getIsp());
        } else {
            alert.setCountry("Unknown");
            alert.setCity("Unknown");
            alert.setIsp("Unknown");
        }

        repository.save(alert);
    }

    public void createAlert(String ip, String endpoint, String attackType, String severity, LocalDateTime time) {
        SecurityAlert alert = new SecurityAlert();
        alert.setIp(ip);
        alert.setEndpoint(endpoint);
        alert.setAttackType(attackType);
        alert.setSeverity(severity);
        alert.setTime(time);

        createAlertWithLocation(alert);
    }
}