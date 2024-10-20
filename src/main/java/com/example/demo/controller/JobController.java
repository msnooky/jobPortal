package com.example.demo.controller;

import com.example.demo.dto.JobDto;
import com.example.demo.dto.SearchDto;
import com.example.demo.models.Freelancer;
import com.example.demo.models.Job;
import com.example.demo.service.JobService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private JwtUtil util;

    private String getUsername(String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        String username = util.extractUsername(jwt);
        return username;
    }

    @GetMapping("/getJobs")
    public List<JobDto> getJobs(@RequestHeader("Authorization") String authorizationHeader) {
        String username = getUsername(authorizationHeader);;
        return jobService.getAllJobs(username);
    }

    @PostMapping("/applyJob")
    public ResponseEntity<String> applyJob(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Long jobId) {
        String username = getUsername(authorizationHeader);
        return jobService.applyForJob(username, jobId);
    }

    @GetMapping("/getApplicants")
    public List<Freelancer> getApplicants(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Long jobId) {
        String username = getUsername(authorizationHeader);
        return jobService.getApplicants(username, jobId);
    }

    @PostMapping("/create")
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        return new ResponseEntity<>(jobService.createJob(job), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job job) {
        return new ResponseEntity<>(jobService.updateJob(id, job), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public List<Job> searchJobs(@RequestBody SearchDto searchDto) {
        return jobService.searchJobs(searchDto);
    }
}
