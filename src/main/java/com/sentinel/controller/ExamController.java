package com.sentinel.controller;

import com.sentinel.dto.LoginRequest;
import com.sentinel.module.RequestLog;
import com.sentinel.module.Student;
import com.sentinel.repo.StudentRepository;
import com.sentinel.service.RequestLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExamController {

    @Autowired
    RequestLogService logService;

    @Autowired
    private StudentRepository studentRepository;

    // Student login — covers brute force and wrong password attacks
    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();

        Student student = studentRepository.findByRollNo(request.getUsername());

        if (student != null && student.getPassword().equals(request.getPassword())) {
            logService.requestLog(ip, "/api/auth/login", "POST", "SUCCESS");
            return ResponseEntity.ok("LOGIN SUCCESS");
        }

        logService.requestLog(ip, "/api/auth/login", "POST", "FAILED");
        return ResponseEntity.status(401).body("INVALID CREDENTIALS");
    }

    // Exam submit — covers rate limiting attack
    @PostMapping("/exam/submit")
    public String submitExam(@RequestBody String answer) {
        return "EXAM SUBMITTED";
    }

    // Result fetch — covers repeated refresh and scraping
    @GetMapping("/result/{rollNo}")
    public String getResult(@PathVariable String rollNo) {
        return "RESULT: " + rollNo + " — 85%";
    }

    // Honeypot — answer key, any access triggers block
    @GetMapping("/answer-key/download")
    public String answerKey() {
        return "ACCESS DENIED";
    }

    // Honeypot — question paper, any access triggers block
    @GetMapping("/exam/question-paper")
    public String questionPaper() {
        return "ACCESS DENIED";
    }

}