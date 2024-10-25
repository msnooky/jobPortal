package com.jobportal.service;

import com.jobportal.models.FreelancerSkillMapping;
import com.jobportal.repository.FreelancerSkillMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FreelancerSkillMappingServiceTest {

    @InjectMocks
    private FreelancerSkillMappingService freelancerSkillMappingService;

    @Mock
    private FreelancerSkillMappingRepository freelancerSkillMappingRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void upsertFreelancerSkill_ShouldInsertNewSkills_WhenNoExistingSkills() {
        Long freelancerId = 1L;
        List<String> skills = Arrays.asList("Java", "Spring", "Hibernate");

        when(freelancerSkillMappingRepository.findByFreelancerId(freelancerId)).thenReturn(Collections.emptyList());

        freelancerSkillMappingService.upsertFreelancerSkill(freelancerId, skills);

        verify(freelancerSkillMappingRepository, times(1)).saveAll(any());
    }

    @Test
    void upsertFreelancerSkill_ShouldInsertOnlyNewSkills_WhenSomeSkillsAlreadyExist() {
        Long freelancerId = 1L;
        List<String> newSkills = Arrays.asList("Java", "Spring", "Hibernate");
        List<FreelancerSkillMapping> existingMappings = Arrays.asList(
                FreelancerSkillMapping.builder().skill("Java").freelancerId(freelancerId).build(),
                FreelancerSkillMapping.builder().skill("JavaScript").freelancerId(freelancerId).build()
        );

        when(freelancerSkillMappingRepository.findByFreelancerId(freelancerId)).thenReturn(existingMappings);

        freelancerSkillMappingService.upsertFreelancerSkill(freelancerId, newSkills);

        verify(freelancerSkillMappingRepository, times(1)).saveAll(any());
    }

    @Test
    void upsertFreelancerSkill_ShouldNotSave_WhenNoNewSkillsProvided() {
        Long freelancerId = 1L;
        List<String> existingSkills = Arrays.asList("Java", "Spring");
        List<FreelancerSkillMapping> existingMappings = existingSkills.stream()
                .map(skill -> FreelancerSkillMapping.builder().skill(skill).freelancerId(freelancerId).build())
                .toList();

        when(freelancerSkillMappingRepository.findByFreelancerId(freelancerId)).thenReturn(existingMappings);

        freelancerSkillMappingService.upsertFreelancerSkill(freelancerId, existingSkills);

        verify(freelancerSkillMappingRepository, times(1)).saveAll(any());
    }

    @Test
    void getSkillsForFreelancer_ShouldReturnSkills() {
        Long freelancerId = 1L;
        List<FreelancerSkillMapping> mappings = Arrays.asList(
                FreelancerSkillMapping.builder().skill("Java").freelancerId(freelancerId).build(),
                FreelancerSkillMapping.builder().skill("Spring").freelancerId(freelancerId).build()
        );

        when(freelancerSkillMappingRepository.findByFreelancerId(freelancerId)).thenReturn(mappings);

        List<String> skills = freelancerSkillMappingService.getSkillsForFreelancer(freelancerId);

        assert skills.size() == 2;
        assert skills.contains("Java");
        assert skills.contains("Spring");
    }

    @Test
    void getSkillsForFreelancer_ShouldReturnEmptyList_WhenNoSkillsFound() {
        Long freelancerId = 1L;

        when(freelancerSkillMappingRepository.findByFreelancerId(freelancerId)).thenReturn(Collections.emptyList());

        List<String> skills = freelancerSkillMappingService.getSkillsForFreelancer(freelancerId);

        assert skills.isEmpty();
    }
}
