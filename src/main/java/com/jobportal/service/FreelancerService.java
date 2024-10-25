package com.jobportal.service;

import com.jobportal.dto.FreelancerDto;
import com.jobportal.models.*;
import com.jobportal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Service
public class FreelancerService {

    @Autowired
    private FreelancerSkillMappingService freelancerSkillMappingService;

    @Autowired
    JobService jobService;

    @Autowired
    UserService userService;

    @Autowired
    FreelancerVisibilityService freelancerVisibilityService;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private FreelancerJobMappingRepository freelancerJobMappingRepository;


    public String createFreelancer(FreelancerDto dto, String username) {
        User user = userService.findUserByUsername(username);
        if(user.getRole().equalsIgnoreCase("Freelancer")) {
            Optional<Freelancer> newFreelancer = freelancerRepository.findByUserId(user.getId());
            if (newFreelancer.isEmpty()) {
                Freelancer freelancer = Freelancer.builder()
                        .location(dto.getLocation())
                        .salary(dto.getSalary())
                        .userId(user.getId()).build();
                freelancerRepository.save(freelancer);
                return "Freelancer created successfully";
            } else {
                return "Freelancer already exists";
            }
        } else {
            return "Not authorized as a Freelancer";
        }
    }

    public String updateFreelancer(String username, FreelancerDto freelancerDto) {
        User user = userService.findUserByUsername(username);
        Freelancer freelancerEntity = freelancerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
        if (!ObjectUtils.isEmpty(freelancerDto.getSkills())) {
            freelancerSkillMappingService.upsertFreelancerSkill(freelancerEntity.getFreelancerId(),
                    freelancerDto.getSkills());
        }
        if (!ObjectUtils.isEmpty(freelancerDto.getSalary())) {
            freelancerEntity.setSalary(freelancerDto.getSalary());
            freelancerRepository.save(freelancerEntity);
        }
        return "Freelancer successfully updated";

    }

    public List<FreelancerDto> getAllFreelancers() {
        List<Freelancer> freelancers = freelancerRepository.findAll();
        return freelancerVisibilityService.getVisibilityOfFreelancers(freelancers);
    }

    public String applyForJob(String username, Long jobId) {
        User user = userService.findUserByUsername(username);
        if(user.getRole().equalsIgnoreCase("Freelancer")) {
            Optional<Job> job = jobService.findJob(jobId);
            if (job.isPresent()) {
                Optional<FreelancerJobMapping> existingMapping = freelancerJobMappingRepository
                        .findByFreelancerIdAndJobId(user.getId(), jobId);
                if (existingMapping.isPresent()) {
                    return "You have already applied for this job.";
                }
                FreelancerJobMapping mapping = FreelancerJobMapping.builder()
                        .jobId(jobId)
                        .freelancerId(user.getId()).build();
                freelancerJobMappingRepository.save(mapping);
                return "Successfully applied";
            } else {
                return "No job found with the job id: " + jobId;
            }
        } else {
            throw new RuntimeException("Unauthorized to apply on a job");
        }
    }

    public List<FreelancerDto> getFreelancersByIds(List<Long> employeeIds) {
        List<Freelancer> freelancers = freelancerRepository.findByFreelancerIdIn(employeeIds);
        return freelancerVisibilityService.getVisibilityOfFreelancers(freelancers);
    }
}
