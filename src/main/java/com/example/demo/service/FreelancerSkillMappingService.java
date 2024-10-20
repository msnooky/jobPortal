package com.example.demo.service;


import com.example.demo.models.FreelancerSkillMapping;
import com.example.demo.repository.FreelancerSkillMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FreelancerSkillMappingService {

    @Autowired
    private FreelancerSkillMappingRepository freelancerSkillMappingRepository;

    @Transactional
    public void upsertFreelancerSkill(Long freelancerId, List<String> skills) {
        // First, check if the record exists
        List<FreelancerSkillMapping> existingMapping = freelancerSkillMappingRepository
                .findByFreelancerId(freelancerId);
        if (!existingMapping.isEmpty()) {
            // Record exists, perform update
            List<String> filteredSkills = skills.stream().filter(s -> existingMapping.stream()
                    .noneMatch(freelancerSkillMapping -> freelancerSkillMapping.getSkill().equalsIgnoreCase(s))).toList();
            List<FreelancerSkillMapping> freelancerSkillMappings = filteredSkills.stream().map(s -> FreelancerSkillMapping.builder()
                    .skill(s)
                    .freelancerId(freelancerId).build()).collect(Collectors.toList());
            freelancerSkillMappingRepository.saveAll(freelancerSkillMappings);
        } else {
            List<FreelancerSkillMapping> freelancerSkillMappings = skills.stream().map(s -> FreelancerSkillMapping.builder()
                    .skill(s)
                    .freelancerId(freelancerId).build()).collect(Collectors.toList());
            freelancerSkillMappingRepository.saveAll(freelancerSkillMappings);
        }
    }
}
