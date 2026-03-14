package com.sentinel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HoneyPotController {

    @GetMapping("/admin/secret")

    public String trap(){

        return "This is secret";

    }

}
