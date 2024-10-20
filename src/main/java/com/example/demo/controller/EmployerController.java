package com.example.demo.controller;

import com.example.demo.dto.EmployerDto;
import com.example.demo.dto.FreelancerDto;
import com.example.demo.dto.JobDto;
import com.example.demo.models.Employer;
import com.example.demo.models.Freelancer;
import com.example.demo.models.Job;
import com.example.demo.service.EmployerService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    @Autowired
    private EmployerService employerService;

    @Autowired
    private JwtUtil util;

    @PostMapping("/create")
    public ResponseEntity<String> createEmployer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody EmployerDto employer) {
        String username = getUsername(authorizationHeader);
        return employerService.createEmployer(employer, username);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateEmployer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody EmployerDto employer) {
        String username = getUsername(authorizationHeader);
        return employerService.updateEmployer(employer, username);
    }

    @PutMapping("/delete")
    public ResponseEntity<String> deleteEmployer(@RequestHeader("Authorization") String authorizationHeader) {
        String username = getUsername(authorizationHeader);
        return employerService.deleteEmployer(username);
    }

    private String getUsername(String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        String username = util.extractUsername(jwt);
        return username;
    }

    @GetMapping("/jobs")
    public List<JobDto> getPostedJobs(@RequestHeader("Authorization") String authorizationHeader,
                                      @RequestParam(value = "id",required = false) Long id) {
        String username = getUsername(authorizationHeader);
        List<JobDto> jobs = employerService.getEmployerJobs(username);
        return Objects.nonNull(id) ?
                jobs.stream().filter(jobDto -> jobDto.getId().equals(id)).collect(Collectors.toList())
                : jobs;
    }

    @PostMapping("/jobs")
    public ResponseEntity<String> postJobs(@RequestHeader("Authorization") String authorizationHeader, @RequestBody JobDto jobDto) {
        String username = getUsername(authorizationHeader);
        return employerService.postJobs(username, jobDto);
    }

    @DeleteMapping("/jobs")
    public ResponseEntity<String> deleteJobs(@RequestBody Long jobId) {
        return employerService.deleteJob(jobId);
    }

}
