package com.sentinel.repo;

import com.sentinel.module.IpLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpLocationRepository extends JpaRepository<IpLocation, String> {
}