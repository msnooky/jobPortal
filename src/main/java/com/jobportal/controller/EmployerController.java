package com.jobportal.controller;

import com.jobportal.dto.EmployerDto;
import com.jobportal.dto.FreelancerDto;
import com.jobportal.dto.JobDto;
import com.jobportal.service.EmployerService;
import com.jobportal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employer")
@CrossOrigin(origins = "http://localhost:3000/")
public class EmployerController {

    @Autowired
    private EmployerService employerService;

    @Autowired
    private JwtUtil util;

    @PostMapping("/create")
    public ResponseEntity<String> createEmployer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody EmployerDto employer) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(employerService.createEmployer(employer, username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateEmployer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody EmployerDto employer) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(employerService.updateEmployer(employer, username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteEmployer(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(employerService.deleteEmployer(username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/getJobs")
    public ResponseEntity<List<JobDto>> getPostedJobs(@RequestHeader("Authorization") String authorizationHeader,
                                                      @RequestParam(value = "id", required = false) Long id) {
        try {
            String username = getUsername(authorizationHeader);
            List<JobDto> jobs = employerService.getEmployerJobs(username);
            return Objects.nonNull(id) ?
                    ResponseEntity.ok(jobs.stream().filter(jobDto -> jobDto.getId().equals(id)).collect(Collectors.toList()))
                    : ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

    }

    @PostMapping("/postJob")
    public ResponseEntity<String> postJob(@RequestHeader("Authorization") String authorizationHeader, @RequestBody JobDto jobDto) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(employerService.postJobs(username, jobDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @DeleteMapping("/deleteJob")
    public ResponseEntity<String> deleteJobs(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Long jobId) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(employerService.deleteJob(username, jobId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/acceptApplication")
    public ResponseEntity<String> acceptApplication(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Long freelancerId) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(employerService.acceptApplication(username, freelancerId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/getEmployees")
    public ResponseEntity<List<FreelancerDto>> getEmployees(@RequestHeader("Authorization") String authorizationHeader,
                                                    @RequestParam(value = "name", required = false) String name) {
        try {
            String username = getUsername(authorizationHeader);
            List<FreelancerDto> employees = employerService.getEmployees(username);
            return Objects.nonNull(name) ?
                    ResponseEntity.ok(employees.stream().filter(employee -> employee.getName().equals(name)).collect(Collectors.toList()))
                    : ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    private String getUsername(String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        return util.extractUsername(jwt);
    }
}
