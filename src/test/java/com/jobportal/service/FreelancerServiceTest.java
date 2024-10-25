package com.jobportal.service;

import com.jobportal.dto.FreelancerDto;
import com.jobportal.models.*;
import com.jobportal.repository.FreelancerJobMappingRepository;
import com.jobportal.repository.FreelancerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FreelancerServiceTest {

    @InjectMocks
    private FreelancerService freelancerService;

    @Mock
    private FreelancerSkillMappingService freelancerSkillMappingService;

    @Mock
    private JobService jobService;

    @Mock
    private UserService userService;

    @Mock
    private FreelancerVisibilityService freelancerVisibilityService;

    @Mock
    private FreelancerRepository freelancerRepository;

    @Mock
    private FreelancerJobMappingRepository freelancerJobMappingRepository;

    private User user;
    private Freelancer freelancer;
    private FreelancerDto freelancerDto;
    private Job job;
    private FreelancerJobMapping jobMapping;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock data
        user = new User();
        user.setId(1L);
        user.setName("freelancerUser");
        user.setRole("Freelancer");

        freelancer = new Freelancer();
        freelancer.setFreelancerId(100L);
        freelancer.setUserId(1L);
        freelancer.setLocation("New York");
        freelancer.setSalary(50000L);

        freelancerDto = new FreelancerDto("Alice", Arrays.asList("Java", "Spring"), 60000L, "New York");
        job = Job.builder().jobId(1L).title("Test Job").build();

        jobMapping = FreelancerJobMapping.builder().jobId(1L).freelancerId(100L).build();
    }

    @Test
    void testCreateFreelancer_Success() {
        when(userService.findUserByUsername("freelancerUser")).thenReturn(user);
        when(freelancerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        String result = freelancerService.createFreelancer(freelancerDto, "freelancerUser");

        assertEquals("Freelancer created successfully", result);
        verify(freelancerRepository).save(any(Freelancer.class));
    }

    @Test
    void testCreateFreelancer_AlreadyExists() {
        when(userService.findUserByUsername("freelancerUser")).thenReturn(user);
        when(freelancerRepository.findByUserId(1L)).thenReturn(Optional.of(freelancer));

        String result = freelancerService.createFreelancer(freelancerDto, "freelancerUser");

        assertEquals("Freelancer already exists", result);
        verify(freelancerRepository, never()).save(any(Freelancer.class));
    }

    @Test
    void testCreateFreelancer_NotAuthorized() {
        user.setRole("Employer");
        when(userService.findUserByUsername("freelancerUser")).thenReturn(user);

        String result = freelancerService.createFreelancer(freelancerDto, "freelancerUser");

        assertEquals("Not authorized as a Freelancer", result);
    }

    @Test
    void testUpdateFreelancer_Success() {
        when(userService.findUserByUsername("freelancerUser")).thenReturn(user);
        when(freelancerRepository.findByUserId(1L)).thenReturn(Optional.of(freelancer));

        String result = freelancerService.updateFreelancer("freelancerUser", freelancerDto);

        assertEquals("Freelancer successfully updated", result);
        verify(freelancerSkillMappingService).upsertFreelancerSkill(freelancer.getFreelancerId(), freelancerDto.getSkills());
        verify(freelancerRepository).save(freelancer);
    }

    @Test
    void testUpdateFreelancer_FreelancerNotFound() {
        when(userService.findUserByUsername("freelancerUser")).thenReturn(user);
        when(freelancerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> freelancerService.updateFreelancer("freelancerUser", freelancerDto));
    }

    @Test
    void testGetAllFreelancers_Success() {
        when(freelancerRepository.findAll()).thenReturn(List.of(freelancer));
        when(freelancerVisibilityService.getVisibilityOfFreelancers(List.of(freelancer)))
                .thenReturn(List.of(freelancerDto));

        List<FreelancerDto> result = freelancerService.getAllFreelancers();

        assertEquals(1, result.size());
        assertEquals(freelancerDto.getLocation(), result.get(0).getLocation());
    }

    @Test
    void testApplyForJob_Success() {
        when(userService.findUserByUsername("freelancerUser")).thenReturn(user);
        when(jobService.findJob(1L)).thenReturn(Optional.of(job));
        when(freelancerJobMappingRepository.findByFreelancerIdAndJobId(user.getId(), 1L))
                .thenReturn(Optional.empty());

        String result = freelancerService.applyForJob("freelancerUser", 1L);

        assertEquals("Successfully applied", result);
        verify(freelancerJobMappingRepository).save(any(FreelancerJobMapping.class));
    }

    @Test
    void testApplyForJob_JobNotFound() {
        when(userService.findUserByUsername("freelancerUser")).thenReturn(user);
        when(jobService.findJob(1L)).thenReturn(Optional.empty());

        String result = freelancerService.applyForJob("freelancerUser", 1L);

        assertEquals("No job found with the job id: 1", result);
        verify(freelancerJobMappingRepository, never()).save(any(FreelancerJobMapping.class));
    }

    @Test
    void testApplyForJob_AlreadyApplied() {
        when(userService.findUserByUsername("freelancerUser")).thenReturn(user);
        when(jobService.findJob(1L)).thenReturn(Optional.of(job));
        when(freelancerJobMappingRepository.findByFreelancerIdAndJobId(user.getId(), 1L))
                .thenReturn(Optional.of(jobMapping));

        String result = freelancerService.applyForJob("freelancerUser", 1L);

        assertEquals("You have already applied for this job.", result);
    }

    @Test
    void testApplyForJob_Unauthorized() {
        user.setRole("Employer");
        when(userService.findUserByUsername("freelancerUser")).thenReturn(user);

        assertThrows(RuntimeException.class, () -> freelancerService.applyForJob("freelancerUser", 1L));
    }

    @Test
    void testGetFreelancersByIds_Success() {
        List<Long> freelancerIds = List.of(100L);
        when(freelancerRepository.findByFreelancerIdIn(freelancerIds)).thenReturn(List.of(freelancer));
        when(freelancerVisibilityService.getVisibilityOfFreelancers(List.of(freelancer)))
                .thenReturn(List.of(freelancerDto));

        List<FreelancerDto> result = freelancerService.getFreelancersByIds(freelancerIds);

        assertEquals(1, result.size());
        assertEquals("New York", result.get(0).getLocation());
    }
}
