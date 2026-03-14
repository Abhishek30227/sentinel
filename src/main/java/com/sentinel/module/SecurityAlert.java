package com.sentinel.module;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "security_alert")
public class SecurityAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    private String ip;
    private String endpoint;
    private String attackType;
    private String severity;
    private LocalDateTime time;

    private String country;
    private String city;
    private String isp;
}