package com.jobportal.controller;

import com.jobportal.dto.FreelancerDto;
import com.jobportal.service.FreelancerService;
import com.jobportal.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FreelancerControllerTest {

    @InjectMocks
    private FreelancerController freelancerController;

    @Mock
    private FreelancerService freelancerService;

    @Mock
    private JwtUtil jwtUtil;

    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorizationHeader = "Bearer valid.jwt.token";
    }

    @Test
    void createFreelancer_ShouldReturnOk() {
        FreelancerDto freelancerDto = new FreelancerDto("name", new ArrayList<>(),
                1000L, "location");
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(freelancerService.createFreelancer(any(), anyString())).thenReturn("Freelancer created");

        ResponseEntity<String> response = freelancerController.createFreelancer(authorizationHeader,
                freelancerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Freelancer created", response.getBody());
    }

    @Test
    void createFreelancer_ShouldReturnUnauthorized_WhenExceptionThrown() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(freelancerService.createFreelancer(any(), anyString())).thenThrow(
                new RuntimeException("Unauthorized"));

        ResponseEntity<String> response = freelancerController.createFreelancer(authorizationHeader,
                new FreelancerDto("name", new ArrayList<>(), 1000L, "location"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    void updateFreelancer_ShouldReturnOk() {
        FreelancerDto freelancerDto = new FreelancerDto("name", new ArrayList<>(),
                1000L, "location");
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(freelancerService.updateFreelancer(anyString(), any())).thenReturn("Freelancer updated");

        ResponseEntity<String> response = freelancerController.updateFreelancer(authorizationHeader,
                freelancerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Freelancer updated", response.getBody());
    }

    @Test
    void updateFreelancer_ShouldReturnUnauthorized_WhenExceptionThrown() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(freelancerService.updateFreelancer(anyString(), any())).thenThrow(
                new RuntimeException("Unauthorized"));

        ResponseEntity<String> response = freelancerController.updateFreelancer(authorizationHeader,
                new FreelancerDto("name", new ArrayList<>(), 1000L, "location"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    void applyJob_ShouldReturnOk() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(freelancerService.applyForJob(anyString(), anyLong())).thenReturn("Job applied successfully");

        ResponseEntity<String> response = freelancerController.applyJob(authorizationHeader, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Job applied successfully", response.getBody());
    }

    @Test
    void applyJob_ShouldReturnUnauthorized_WhenExceptionThrown() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(freelancerService.applyForJob(anyString(), anyLong())).thenThrow(
                new RuntimeException("Unauthorized"));

        ResponseEntity<String> response = freelancerController.applyJob(authorizationHeader, 1L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody());
    }
}
