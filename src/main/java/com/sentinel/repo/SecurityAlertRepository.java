package com.sentinel.repo;

import com.sentinel.module.SecurityAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecurityAlertRepository extends JpaRepository<SecurityAlert, Long> {

    List<SecurityAlert> findAllByOrderByTimeDesc();
}
