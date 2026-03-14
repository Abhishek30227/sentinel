package com.sentinel.module;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "ip_location")
@Data
@Entity
public class IpLocation {

    @Id // Primary key — IP address is unique
    private String ip;

    private String country;
    private String city;
    private String isp;

    // Coordinates for map markers
    private Double lat;
    private Double lng;
}