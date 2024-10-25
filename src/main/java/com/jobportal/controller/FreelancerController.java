package com.jobportal.controller;

import com.jobportal.dto.FreelancerDto;
import com.jobportal.service.FreelancerService;
import com.jobportal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/freelancer")
@CrossOrigin(origins = "http://localhost:3000/")
public class FreelancerController {

    @Autowired
    private FreelancerService freelancerService;

    @Autowired
    private JwtUtil util;

    @PostMapping("/create")
    public ResponseEntity<String> createFreelancer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody FreelancerDto freelancer) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(freelancerService.createFreelancer(freelancer, username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @PutMapping("/update")
    public ResponseEntity<String> updateFreelancer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody FreelancerDto freelancer) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(freelancerService.updateFreelancer(username, freelancer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/applyJob")
    public ResponseEntity<String> applyJob(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Long jobId) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(freelancerService.applyForJob(username, jobId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    private String getUsername(String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        return util.extractUsername(jwt);
    }
}
