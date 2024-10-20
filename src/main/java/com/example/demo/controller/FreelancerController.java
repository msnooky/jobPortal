package com.example.demo.controller;

import com.example.demo.dto.FreelancerDto;
import com.example.demo.models.Freelancer;
import com.example.demo.service.FreelancerService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/freelancer")
public class FreelancerController {

    @Autowired
    private FreelancerService freelancerService;

    @Autowired
    private JwtUtil util;

    private String getUsername(String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        String username = util.extractUsername(jwt);
        return username;
    }

    @PostMapping("/create")
    public Freelancer createFreelancer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody FreelancerDto freelancer) {
        String username = getUsername(authorizationHeader);
        return freelancerService.createFreelancer(freelancer, username);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateFreelancer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody FreelancerDto freelancer) {
        String username = getUsername(authorizationHeader);;
        return freelancerService.updateFreelancer(username, freelancer);
    }

    @GetMapping("/view")
    public List<Freelancer> getAllFreelancers() {
        return freelancerService.getAllFreelancers();
    }
}
