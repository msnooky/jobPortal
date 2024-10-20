package com.example.demo.service;

import com.example.demo.dto.FreelancerDto;
import com.example.demo.models.Freelancer;
import com.example.demo.models.User;
import com.example.demo.repository.FreelancerRepository;
import com.example.demo.repository.FreelancerSkillMappingRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Service
public class FreelancerService {
    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FreelancerSkillMappingService freelancerSkillMappingService;

    @Autowired
    private FreelancerSkillMappingRepository freelancerSkillMappingRepository;

    public Freelancer createFreelancer(FreelancerDto dto, String username) {
        User user = userRepository.findByName(username).get();

        Freelancer freelancer = Freelancer.builder()
                .location(dto.getLocation())
                .salary(dto.getSalary())
                .userId(user.getId()).build();
        return freelancerRepository.save(freelancer);
    }

    public ResponseEntity<String> updateFreelancer(String userName, FreelancerDto freelancerDto) {
        try {
            User user = userRepository.findByName(userName).get();
            Optional<Freelancer> freelancerEntity = freelancerRepository.findByUserId(user.getId());
            if (!ObjectUtils.isEmpty(freelancerDto.getSkills()) && freelancerEntity.isPresent()) {
                freelancerSkillMappingService.upsertFreelancerSkill(freelancerEntity.get().getFreelancerId(),
                        freelancerDto.getSkills());
            }
            if (!ObjectUtils.isEmpty(freelancerDto.getSalary()) && freelancerEntity.isPresent()) {
                Freelancer freelancer = freelancerEntity.get();
                freelancer.setSalary(freelancerDto.getSalary());
                freelancerRepository.save(freelancer);
            }
            return ResponseEntity.ok("Freelancer successfully updated");

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }

    public List<Freelancer> getAllFreelancers() {
        return freelancerRepository.findAll();
    }
}
