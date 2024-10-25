package com.jobportal.service;

import com.jobportal.dto.EmployerDto;
import com.jobportal.dto.FreelancerDto;
import com.jobportal.dto.JobDto;
import com.jobportal.models.Employer;
import com.jobportal.models.EmployerEmployeeMapping;
import com.jobportal.models.Job;
import com.jobportal.models.User;
import com.jobportal.repository.EmployerEmployeeMappingRepository;
import com.jobportal.repository.EmployerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployerServiceTest {

    @InjectMocks
    private EmployerService employerService;

    @Mock
    private FreelancerService freelancerService;

    @Mock
    private JobService jobService;

    @Mock
    private UserService userService;

    @Mock
    private FreelancerVisibilityService freelancerVisibilityService;

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private EmployerEmployeeMappingRepository employerEmployeeMappingRepository;

    private User user;
    private Employer employer;
    private EmployerDto employerDto;
    private JobDto jobDto;
    private Job job;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("testuser");
        user.setRole("Employer");

        employer = new Employer();
        employer.setEmployerId(10L);
        employer.setUserId(1L);
        employer.setCompanyName("Test Company");

        employerDto = new EmployerDto("Updated Company");

        jobDto = new JobDto(1L, "Test Job", "Job Description",
                "New York", 60000L, "Java", Arrays.asList("Java", "Spring"));

        job = Job.builder()
                .jobId(1L)
                .employerId(10L)
                .title("Test Job")
                .description("Job Description")
                .location("New York")
                .salary(60000L)
                .tags("Java")
                .build();
    }

    @Test
    void testCreateEmployer_Success() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        String result = employerService.createEmployer(employerDto, "testuser");

        assertEquals("Employer created successfully", result);
        verify(employerRepository).save(any(Employer.class));
    }

    @Test
    void testCreateEmployer_AlreadyExists() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));

        String result = employerService.createEmployer(employerDto, "testuser");

        assertEquals("Employer already exists", result);
        verify(employerRepository, never()).save(any(Employer.class));
    }

    @Test
    void testCreateEmployer_NotAuthorized() {
        user.setRole("Freelancer");
        when(userService.findUserByUsername("testuser")).thenReturn(user);

        assertThrows(RuntimeException.class, () -> employerService.createEmployer(employerDto, "testuser"));
    }

    @Test
    void testUpdateEmployer_Success() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));

        String result = employerService.updateEmployer(employerDto, "testuser");

        assertEquals("Successfully Updated", result);
        verify(employerRepository).save(employer);
    }

    @Test
    void testUpdateEmployer_NotAuthorized() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employerService.updateEmployer(employerDto, "testuser"));
    }

    @Test
    void testGetEmployerJobs_Success() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));
        when(jobService.getJobsByEmployerId(10L)).thenReturn(List.of(job));

        List<JobDto> result = employerService.getEmployerJobs("testuser");

        assertEquals(1, result.size());
        assertEquals("Test Job", result.get(0).getTitle());
        verify(jobService).getJobsByEmployerId(10L);
    }

    @Test
    void testGetEmployerJobs_NotAuthorized() {
        user.setRole("Freelancer");
        when(userService.findUserByUsername("testuser")).thenReturn(user);

        assertThrows(RuntimeException.class, () -> employerService.getEmployerJobs("testuser"));
    }

    @Test
    void testDeleteEmployer_Success() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));

        String result = employerService.deleteEmployer("testuser");

        assertEquals("Successfully Deleted", result);
        verify(employerRepository).deleteById(10L);
        verify(userService).deleteEmployer(1L);
    }

    @Test
    void testDeleteEmployer_NotAuthorized() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employerService.deleteEmployer("testuser"));
    }

    @Test
    void testPostJobs_Success() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));

        String result = employerService.postJobs("testuser", jobDto);

        assertEquals("Successfully Posted job", result);
        verify(jobService).createJob(any(Job.class), eq(jobDto.getSkills()));
    }

    @Test
    void testPostJobs_NotAuthorized() {
        user.setRole("Freelancer");
        when(userService.findUserByUsername("testuser")).thenReturn(user);

        assertThrows(RuntimeException.class, () -> employerService.postJobs("testuser", jobDto));
    }

    @Test
    void testDeleteJob_Success() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));

        when(jobService.deleteJob(employer, 1L)).thenReturn("Deleted");

        String result = employerService.deleteJob("testuser", 1L);

        assertEquals("Deleted", result);
        verify(jobService).deleteJob(employer, 1L);
    }

    @Test
    void testDeleteJob_NotAuthorized() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employerService.deleteJob("testuser", 1L));
    }

    @Test
    void testAcceptApplication_Success() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));
        when(employerEmployeeMappingRepository.existsByEmployerIdAndEmployeeId(10L, 100L)).thenReturn(false);

        String result = employerService.acceptApplication("testuser", 100L);

        assertEquals("Freelancer added as an Employee", result);
        verify(employerEmployeeMappingRepository).save(any(EmployerEmployeeMapping.class));
    }

    @Test
    void testAcceptApplication_FreelancerAlreadyAdded() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));
        when(employerEmployeeMappingRepository.existsByEmployerIdAndEmployeeId(10L, 100L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> employerService.acceptApplication("testuser", 100L));
    }

    @Test
    void testGetEmployees_Success() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));
        when(employerEmployeeMappingRepository.findEmployeeIdByEmployerId(10L)).thenReturn(List.of(100L));
        when(freelancerService.getFreelancersByIds(List.of(100L))).thenReturn(List.of(new FreelancerDto("User", new ArrayList<>(), 1000L, "Mumbai")));

        List<FreelancerDto> result = employerService.getEmployees("testuser");

        assertEquals(1, result.size());
        verify(freelancerService).getFreelancersByIds(List.of(100L));
    }

    @Test
    void testGetEmployees_NotAuthorized() {
        when(userService.findUserByUsername("testuser")).thenReturn(user);
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employerService.getEmployees("testuser"));
    }
}
