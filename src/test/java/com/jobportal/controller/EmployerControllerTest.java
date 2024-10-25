package com.jobportal.controller;

import com.jobportal.dto.EmployerDto;
import com.jobportal.dto.FreelancerDto;
import com.jobportal.dto.JobDto;
import com.jobportal.service.EmployerService;
import com.jobportal.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EmployerControllerTest {

    @InjectMocks
    private EmployerController employerController;

    @Mock
    private EmployerService employerService;

    @Mock
    private JwtUtil jwtUtil;

    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorizationHeader = "Bearer valid.jwt.token";
    }

    @Test
    void createEmployer_ShouldReturnOk() {
        EmployerDto employerDto = new EmployerDto("Dummy");
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(employerService.createEmployer(any(), anyString())).thenReturn("Employer created");

        ResponseEntity<String> response = employerController.createEmployer(authorizationHeader, employerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employer created", response.getBody());
    }

    @Test
    void createEmployer_ShouldReturnUnauthorized_WhenExceptionThrown() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(employerService.createEmployer(any(), anyString())).thenThrow(new RuntimeException("Unauthorized"));

        ResponseEntity<String> response = employerController.createEmployer(authorizationHeader,
                new EmployerDto("Dummy"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    void updateEmployer_ShouldReturnOk() {
        EmployerDto employerDto = new EmployerDto("Dummy");
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(employerService.updateEmployer(any(), anyString())).thenReturn("Employer updated");

        ResponseEntity<String> response = employerController.updateEmployer(authorizationHeader, employerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employer updated", response.getBody());
    }

    @Test
    void deleteEmployer_ShouldReturnOk() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(employerService.deleteEmployer(anyString())).thenReturn("Employer deleted");

        ResponseEntity<String> response = employerController.deleteEmployer(authorizationHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employer deleted", response.getBody());
    }

    @Test
    void getPostedJobs_ShouldReturnJobs() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(employerService.getEmployerJobs(anyString()))
                .thenReturn(Collections.singletonList(new JobDto(1L, "title", "description",
                        "location", 2000L, "tags", new ArrayList<>())));

        ResponseEntity<List<JobDto>> response = employerController.getPostedJobs(authorizationHeader, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void postJob_ShouldReturnOk() {
        JobDto jobDto = new JobDto(1L, "title", "description", "location",
                2000L, "tags", new ArrayList<>());
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(employerService.postJobs(anyString(), any())).thenReturn("Job posted");

        ResponseEntity<String> response = employerController.postJob(authorizationHeader, jobDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Job posted", response.getBody());
    }

    @Test
    void deleteJob_ShouldReturnOk() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(employerService.deleteJob(anyString(), anyLong())).thenReturn("Job deleted");

        ResponseEntity<String> response = employerController.deleteJobs(authorizationHeader, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Job deleted", response.getBody());
    }

    @Test
    void acceptApplication_ShouldReturnOk() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(employerService.acceptApplication(anyString(), anyLong())).thenReturn("Application accepted");

        ResponseEntity<String> response = employerController.acceptApplication(authorizationHeader, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Application accepted", response.getBody());
    }

    @Test
    void getEmployees_ShouldReturnEmployees() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(employerService.getEmployees(anyString())).thenReturn(Collections.singletonList(
                new FreelancerDto("name", new ArrayList<>(), 1000L, "location")));

        ResponseEntity<List<FreelancerDto>> response = employerController.getEmployees(authorizationHeader, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
