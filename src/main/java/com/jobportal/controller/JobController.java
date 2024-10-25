package com.jobportal.controller;

import com.jobportal.dto.FreelancerDto;
import com.jobportal.dto.JobDto;
import com.jobportal.dto.SearchDto;
import com.jobportal.service.JobService;
import com.jobportal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class JobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private JwtUtil util;

    /**
     * Retrieves all jobs for the authenticated user.
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     * @return ResponseEntity with a list of JobDto objects or an error message.
     */
    @GetMapping("/getJobs")
    public ResponseEntity<List<JobDto>> getJobs(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(jobService.getAllJobs(username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * Retrieves all applicants for a specific job.
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     * @param jobId              The ID of the job.
     * @return ResponseEntity with a list of FreelancerDto objects or an error message.
     */
    @GetMapping("/getApplicants")
    public ResponseEntity<List<FreelancerDto>> getApplicants(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Long jobId) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(jobService.getApplicants(username, jobId));
        } catch (Exception e) {
            return (ResponseEntity<List<FreelancerDto>>) ResponseEntity.badRequest();
        }
    }

    /**
     * Searches for jobs based on the provided search criteria.
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     * @param searchDto           The search criteria.
     * @return ResponseEntity with a list of JobDto objects or an error message.
     */
    @PostMapping("/searchJob")
    public ResponseEntity<List<JobDto>> searchJobs(@RequestHeader("Authorization") String authorizationHeader, @RequestBody SearchDto searchDto) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(jobService.searchJobs(username, searchDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Extracts the username from the authorization header.
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     * @return The extracted username.
     */
    private String getUsername(String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        return util.extractUsername(jwt);
    }
}
