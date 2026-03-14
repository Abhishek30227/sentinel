package com.sentinel.service;

import com.sentinel.dto.GeoLocationResponse;
import com.sentinel.module.IpLocation;
import com.sentinel.repo.IpLocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Service
public class GeoLocationService {

    @Autowired
    private IpLocationRepository repository;

    private final RestClient restClient = RestClient.create();

    @Value("${geo.api.key}")
    private String apiKey;

    @Value("${geo.api.url}")
    private String apiUrl;

    public IpLocation getLocation(String ip) {

        // Check cache first before making API call
        Optional<IpLocation> cached = repository.findById(ip);
        if (cached.isPresent()) {
            System.out.println("CACHE HIT: " + ip);
            return cached.get();
        }

        try {
            String url = apiUrl + "?apiKey=" + apiKey + "&ip=" + ip;

            System.out.println("GEO API CALL: " + url);

            GeoLocationResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(GeoLocationResponse.class);

            System.out.println("GEO API RESPONSE: " + response);

            if (response == null) {
                return null;
            }

            // Map API response to IpLocation entity
            IpLocation location = new IpLocation();
            location.setIp(ip);
            location.setCountry(response.getCountryName());
            location.setCity(response.getCity());
            location.setIsp(response.getIsp());

            // Parse lat/lng from String to Double
            location.setLat(response.getLatitude()  != null ? Double.parseDouble(response.getLatitude())  : null);
            location.setLng(response.getLongitude() != null ? Double.parseDouble(response.getLongitude()) : null);

            return repository.save(location);

        } catch (Exception e) {
            System.out.println("GEO API ERROR: " + e.getMessage());
            return null;
        }
    }
}