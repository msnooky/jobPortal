package com.jobportal.service;

import com.jobportal.dto.FreelancerDto;
import com.jobportal.dto.JobDto;
import com.jobportal.dto.SearchDto;
import com.jobportal.models.*;
import com.jobportal.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobServiceTest {

    @InjectMocks
    private JobService jobService;

    @Mock
    private UserService userService;

    @Mock
    private FreelancerVisibilityService freelancerVisibilityService;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private FreelancerRepository freelancerRepository;

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private JobSkillMappingRepository jobSkillMappingRepository;

    @Mock
    private FreelancerJobMappingRepository freelancerJobMappingRepository;

    private User employerUser;
    private User freelancerUser;
    private Job job;
    private Employer employer;
    private SearchDto searchDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employerUser = new User();
        employerUser.setId(1L);
        employerUser.setName("employerUser");
        employerUser.setRole("Employer");

        freelancerUser = new User();
        freelancerUser.setId(2L);
        freelancerUser.setName("freelancerUser");
        freelancerUser.setRole("Freelancer");

        employer = new Employer();
        employer.setEmployerId(1L);
        employer.setUserId(employerUser.getId());

        job = new Job();
        job.setJobId(1L);
        job.setTitle("Software Developer");
        job.setEmployerId(employer.getEmployerId());
        job.setLocation("New York");
        job.setSalary(60000L);
        job.setTags("IT");

        searchDto = new SearchDto(Arrays.asList("Java", "Spring"), "New York",
                50000L, 70000L);
    }

    @Test
    void testCreateJob() {
        List<String> skills = Arrays.asList("Java", "Spring");

        jobService.createJob(job, skills);

        verify(jobRepository, times(1)).save(job);
        verify(jobSkillMappingRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testSearchJobs_WithFilters() {

        when(userService.findUserByUsername("freelancerUser")).thenReturn(freelancerUser);
        when(jobRepository.findAll()).thenReturn(Collections.singletonList(job));
        when(jobSkillMappingRepository.findAllBySkillIn(anyList())).thenReturn(Collections.singletonList(1L));
        when(jobRepository.findAllByJobIdIn(anyList())).thenReturn(Collections.singletonList(job));
        when(jobRepository.findByLocation("New York")).thenReturn(Collections.singletonList(job));
        when(jobRepository.findBySalaryBetween(anyLong(), anyLong())).thenReturn(Collections.singletonList(job));

        List<JobDto> jobs = jobService.searchJobs("freelancerUser", searchDto);

        assertEquals(1, jobs.size());
        assertEquals("Software Developer", jobs.get(0).getTitle());
    }

    @Test
    void testGetApplicants_Authorized() {
        when(userService.findUserByUsername("employerUser")).thenReturn(employerUser);
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));
        when(freelancerJobMappingRepository.findByJobId(1L)).thenReturn(Collections.emptyList());

        List<FreelancerDto> applicants = jobService.getApplicants("employerUser", 1L);

        assertTrue(applicants.isEmpty());
    }

    @Test
    void testGetApplicants_Unauthorized() {
        when(userService.findUserByUsername("employerUser")).thenReturn(employerUser);
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(employerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> jobService.getApplicants("employerUser", 1L));
    }

    @Test
    void testGetJobsByEmployerId() {
        when(jobRepository.findAllByEmployerId(1L)).thenReturn(Collections.singletonList(job));

        List<Job> jobs = jobService.getJobsByEmployerId(1L);

        assertEquals(1, jobs.size());
        assertEquals("Software Developer", jobs.get(0).getTitle());
    }

    @Test
    void testGetAllJobs_AuthorizedFreelancer() {
        when(userService.findUserByUsername("freelancerUser")).thenReturn(freelancerUser);
        when(jobRepository.findAll()).thenReturn(Collections.singletonList(job));

        List<JobDto> jobs = jobService.getAllJobs("freelancerUser");

        assertEquals(1, jobs.size());
        assertEquals("Software Developer", jobs.get(0).getTitle());
    }

    @Test
    void testGetAllJobs_UnauthorizedUser() {
        when(userService.findUserByUsername("employerUser")).thenReturn(employerUser); // Employer role, not allowed

        assertThrows(RuntimeException.class, () -> jobService.getAllJobs("employerUser"));
    }

    @Test
    void testGetJobSkills() {
        JobSkillMapping jobSkillMapping = new JobSkillMapping();
        jobSkillMapping.setSkill("Java");
        jobSkillMapping.setId(1L);
        when(jobSkillMappingRepository.findByJobId(1L)).thenReturn(Collections.singletonList(jobSkillMapping));

        List<String> skills = jobService.getJobSkills(1L);

        assertEquals(1, skills.size());
        assertEquals("Java", skills.get(0));
    }

    @Test
    void testFindJob() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        Optional<Job> foundJob = jobService.findJob(1L);

        assertTrue(foundJob.isPresent());
        assertEquals("Software Developer", foundJob.get().getTitle());
    }
}
