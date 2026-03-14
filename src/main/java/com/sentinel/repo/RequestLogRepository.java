package com.sentinel.repo;

import com.sentinel.module.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
    
    long countByIpAndEndpointAndStatusAndTimeAfter(String ip, String endpoint, String status, LocalDateTime since);

    long count();

    long countByStatus(String status);

    @Query(value = """
    SELECT DATE_FORMAT(time, '%H:%i') AS minute,
           COUNT(*) AS total
    FROM request_log
    WHERE DATE(time) = CURDATE()
    GROUP BY minute
    ORDER BY minute
    """, nativeQuery = true)
    List<Object[]> getLast10MinutesData();

    long countByIpAndStatusAndTimeAfter(String ip, String success, LocalDateTime time);

    List<RequestLog> findAllByOrderByTimeDesc();

    @Query(value = """
    SELECT endpoint, COUNT(*) as count
    FROM request_log
    GROUP BY endpoint
    ORDER BY count DESC
    LIMIT 5
""", nativeQuery = true)
    List<Object[]> getTopEndpoints();
}

