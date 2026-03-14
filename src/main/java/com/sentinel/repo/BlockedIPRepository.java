package com.sentinel.repo;
import com.sentinel.module.BlockedIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BlockedIPRepository extends JpaRepository<BlockedIp, Long> {

    @Query("SELECT COUNT(b) FROM BlockedIp b WHERE b.blockedTill > CURRENT_TIMESTAMP")
    long countActiveBlocked();

    // Top 5 recently blocked IPs
    List<BlockedIp> findTop5ByOrderByBlockedTillDesc();

    List<BlockedIp> findAllByOrderByBlockedTillDesc();

    BlockedIp findTopByIpOrderByBlockedTillDesc(String ip);

}
