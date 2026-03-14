package com.sentinel.service;

import com.sentinel.module.BlockedIp;
import com.sentinel.repo.BlockedIPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockService {


    @Autowired
    BlockedIPRepository repo;

    //***************************METHODS REQUIRE BY INTERCEPTOR***********************************

    // find blocked ip
    public BlockedIp findByIp(String ip){
        return repo.findTopByIpOrderByBlockedTillDesc(ip); // ← CHANGE
    }


    // Block ip — update if exists, insert if new
    public void blockIp(BlockedIp newBlock) {

        repo.save(newBlock);

    }


    // dashboard blocked count
    public long getBlockedCount(){

        return repo.count();

    }

    //***************************METHODS REQUIRE BY UI***********************************

}