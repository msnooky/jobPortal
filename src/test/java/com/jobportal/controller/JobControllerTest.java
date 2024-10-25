package com.jobportal.controller;

import com.jobportal.dto.FreelancerDto;
import com.jobportal.dto.JobDto;
import com.jobportal.dto.SearchDto;
import com.jobportal.service.JobService;
import com.jobportal.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JobControllerTest {

    @InjectMocks
    private JobController jobController;

    @Mock
    private JobService jobService;

    @Mock
    private JwtUtil jwtUtil;

    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorizationHeader = "Bearer valid.jwt.token";
    }

    @Test
    void getJobs_ShouldReturnOk() {
        List<JobDto> jobDtos = Arrays.asList(new JobDto(1L, "title", "description",
                "location", 2000L, "tags", new ArrayList<>()), new JobDto(1L,
                "title", "description", "location", 2000L,
                "tags", new ArrayList<>()));
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(jobService.getAllJobs(anyString())).thenReturn(jobDtos);

        ResponseEntity<List<JobDto>> response = jobController.getJobs(authorizationHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jobDtos, response.getBody());
    }

    @Test
    void getJobs_ShouldReturnUnauthorized_WhenExceptionThrown() {
        when(jwtUtil.extractUsername(anyString())).thenThrow(new RuntimeException("Unauthorized"));

        ResponseEntity<List<JobDto>> response = jobController.getJobs(authorizationHeader);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getApplicants_ShouldReturnOk() {
        Long jobId = 1L;
        List<FreelancerDto> freelancerDtos = Arrays.asList(new FreelancerDto("name",
                new ArrayList<>(), 1000L, "location"), new FreelancerDto("name",
                new ArrayList<>(), 1000L, "location"));
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(jobService.getApplicants(anyString(), any(Long.class))).thenReturn(freelancerDtos);

        ResponseEntity<List<FreelancerDto>> response = jobController.getApplicants(authorizationHeader, jobId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(freelancerDtos, response.getBody());
    }

    @Test
    void searchJobs_ShouldReturnOk() {
        SearchDto searchDto = new SearchDto(new ArrayList<>(), "location",
                100L, 1000L);
        List<JobDto> jobDtos = Arrays.asList(new JobDto(1L, "title", "description",
                "location", 2000L, "tags", new ArrayList<>()), new JobDto(1L,
                "title", "description", "location", 2000L,
                "tags", new ArrayList<>()));
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUser");
        when(jobService.searchJobs(anyString(), any(SearchDto.class))).thenReturn(jobDtos);

        ResponseEntity<List<JobDto>> response = jobController.searchJobs(authorizationHeader, searchDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jobDtos, response.getBody());
    }

    @Test
    void searchJobs_ShouldReturnBadRequest_WhenExceptionThrown() {
        when(jwtUtil.extractUsername(anyString())).thenThrow(new RuntimeException("Unauthorized"));

        ResponseEntity<List<JobDto>> response = jobController.searchJobs(authorizationHeader,
                new SearchDto(new ArrayList<>(), "location", 100L, 1000L));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
}
