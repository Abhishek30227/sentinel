package com.sentinel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/exam-portal.html";
    }

    @GetMapping("/admin")
    public String admin(){
        return "redirect:/dashboard.html";
    }
}
