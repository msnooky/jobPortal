package com.jobportal.controller;

import com.jobportal.dto.FreelancerDto;
import com.jobportal.service.FreelancerService;
import com.jobportal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Suggested code may be subject to a license. Learn more: ~LicenseLog:2100421316.
// Suggested code may be subject to a license. Learn more: ~LicenseLog:763927218.

@RestController
@RequestMapping("/api/freelancer")
@CrossOrigin(origins = "http://localhost:3000/")
public class FreelancerController {

    @Autowired
    private FreelancerService freelancerService;

    @Autowired
    private JwtUtil util;

    /**
     * Creates a new freelancer profile.
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     * @param freelancer         The FreelancerDto object containing the freelancer details.
     * @return ResponseEntity with the created freelancer's username or an error message.
     */
    @PostMapping("/create")
    public ResponseEntity<String> createFreelancer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody FreelancerDto freelancer) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(freelancerService.createFreelancer(freelancer, username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    /**
     * Updates an existing freelancer profile.
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     * @param freelancer         The FreelancerDto object containing the updated freelancer details.
     * @return ResponseEntity with a success message or an error message.
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateFreelancer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody FreelancerDto freelancer) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(freelancerService.updateFreelancer(username, freelancer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Allows a freelancer to apply for a job.
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     * @param jobId              The ID of the job to apply for.
     * @return ResponseEntity with a success message or an error message.
     */
    @PostMapping("/applyJob")
    public ResponseEntity<String> applyJob(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Long jobId) {
        try {
            String username = getUsername(authorizationHeader);
            return ResponseEntity.ok(freelancerService.applyForJob(username, jobId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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
