package com.jobportal.service;

import com.jobportal.dto.FreelancerDto;
import com.jobportal.models.Freelancer;
import com.jobportal.models.FreelancerVisibility;
import com.jobportal.models.User;
import com.jobportal.repository.FreelancerVisibilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FreelancerVisibilityServiceTest {

    @InjectMocks
    private FreelancerVisibilityService freelancerVisibilityService;

    @Mock
    private UserService userService;

    @Mock
    private FreelancerSkillMappingService freelancerSkillMappingService;

    @Mock
    private FreelancerVisibilityRepository freelancerVisibilityRepository;

    private Freelancer freelancer;
    private User user;
    private FreelancerVisibility freelancerVisibility;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        freelancer = new Freelancer();
        freelancer.setFreelancerId(1L);
        freelancer.setUserId(100L);
        freelancer.setSalary(50000L);
        freelancer.setLocation("New York");

        user = new User();
        user.setId(100L);
        user.setName("John Doe");

        freelancerVisibility = new FreelancerVisibility();
        freelancerVisibility.setFreelancerId(1L);
        freelancerVisibility.setName(true);
        freelancerVisibility.setSkills(true);
        freelancerVisibility.setSalary(true);
        freelancerVisibility.setLocation(false);
    }

    @Test
    void testGetVisibilityOfFreelancers_withFullVisibility() {
        when(freelancerVisibilityRepository.findByFreelancerId(freelancer.getFreelancerId()))
                .thenReturn(Optional.of(freelancerVisibility));

        when(freelancerSkillMappingService.getSkillsForFreelancer(freelancer.getFreelancerId()))
                .thenReturn(List.of("Java", "Spring"));

        when(userService.getNameByUserId(freelancer.getUserId()))
                .thenReturn(Optional.of(user));

        List<FreelancerDto> result = freelancerVisibilityService.getVisibilityOfFreelancers(List.of(freelancer));

        assertEquals(1, result.size());
        FreelancerDto freelancerDto = result.get(0);
        assertEquals("John Doe", freelancerDto.getName());
        assertEquals(List.of("Java", "Spring"), freelancerDto.getSkills());
        assertEquals(50000L, freelancerDto.getSalary());
        assertNull(freelancerDto.getLocation());
    }

    @Test
    void testGetVisibilityOfFreelancers_withoutNameVisibility() {
        freelancerVisibility.setName(false);

        when(freelancerVisibilityRepository.findByFreelancerId(freelancer.getFreelancerId()))
                .thenReturn(Optional.of(freelancerVisibility));

        when(freelancerSkillMappingService.getSkillsForFreelancer(freelancer.getFreelancerId()))
                .thenReturn(List.of("Java", "Spring"));

        when(userService.getNameByUserId(freelancer.getUserId()))
                .thenReturn(Optional.of(user));

        List<FreelancerDto> result = freelancerVisibilityService.getVisibilityOfFreelancers(List.of(freelancer));

        // Assertions
        assertEquals(1, result.size());
        FreelancerDto freelancerDto = result.get(0);
        assertNull(freelancerDto.getName()); // Name visibility is false
        assertEquals(List.of("Java", "Spring"), freelancerDto.getSkills());
        assertEquals(50000L, freelancerDto.getSalary());
        assertNull(freelancerDto.getLocation());
    }

    @Test
    void testGetVisibilityOfFreelancers_withoutSkillsVisibility() {
        freelancerVisibility.setSkills(false);

        when(freelancerVisibilityRepository.findByFreelancerId(freelancer.getFreelancerId()))
                .thenReturn(Optional.of(freelancerVisibility));

        when(userService.getNameByUserId(freelancer.getUserId()))
                .thenReturn(Optional.of(user));

        List<FreelancerDto> result = freelancerVisibilityService.getVisibilityOfFreelancers(List.of(freelancer));

        assertEquals(1, result.size());
        FreelancerDto freelancerDto = result.get(0);
        assertEquals("John Doe", freelancerDto.getName());
        assertNull(freelancerDto.getSkills()); // Skills visibility is false
        assertEquals(50000L, freelancerDto.getSalary());
        assertNull(freelancerDto.getLocation());
    }

    @Test
    void testGetVisibilityOfFreelancers_freelancerVisibilityNotFound() {
        when(freelancerVisibilityRepository.findByFreelancerId(freelancer.getFreelancerId()))
                .thenReturn(Optional.empty());

        // Exception should be thrown
        assertThrows(RuntimeException.class, () -> {
            freelancerVisibilityService.getVisibilityOfFreelancers(List.of(freelancer));
        });
    }

    @Test
    void testGetVisibilityOfFreelancers_userNotFound() {
        when(freelancerVisibilityRepository.findByFreelancerId(freelancer.getFreelancerId()))
                .thenReturn(Optional.of(freelancerVisibility));

        when(freelancerSkillMappingService.getSkillsForFreelancer(freelancer.getFreelancerId()))
                .thenReturn(List.of("Java", "Spring"));

        when(userService.getNameByUserId(freelancer.getUserId()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            freelancerVisibilityService.getVisibilityOfFreelancers(List.of(freelancer));
        });
    }
}
