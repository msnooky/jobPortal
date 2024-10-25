package com.jobportal.service;

import com.jobportal.dto.FreelancerDto;
import com.jobportal.models.Freelancer;
import com.jobportal.models.FreelancerVisibility;
import com.jobportal.models.User;
import com.jobportal.repository.FreelancerVisibilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FreelancerVisibilityService {

    @Autowired
    UserService userService;

    @Autowired
    FreelancerSkillMappingService freelancerSkillMappingService;

    @Autowired
    FreelancerVisibilityRepository freelancerVisibilityRepository;

    /**
 * Retrieves the visibility settings for a list of freelancers.
 *
 * @param freelancers The list of freelancers.
 * @return A list of FreelancerDto objects representing the visibility settings for each freelancer.
 */
    public List<FreelancerDto> getVisibilityOfFreelancers(List<Freelancer> freelancers) {
        return freelancers.stream()
                .map(freelancer -> {
                    FreelancerVisibility visibility = freelancerVisibilityRepository
                            .findByFreelancerId(freelancer.getFreelancerId())
                            .orElseThrow();
                    List<String> skills = fetchSkillsForFreelancer(freelancer.getFreelancerId());
                    User user = userService.getNameByUserId(freelancer.getUserId())
                            .orElseThrow();
                    return new FreelancerDto(
                            visibility.isName() ? user.getName() : null,
                            visibility.isSkills() ? skills : null,
                            visibility.isSalary() ? freelancer.getSalary() : null,
                            visibility.isLocation() ? freelancer.getLocation() : null
                    );
                })
                .collect(Collectors.toList());
    }

/**
 * Fetches the skills for a given freelancer.
 *
 * @param freelancerId The ID of the freelancer.
 * @return A list of skills for the freelancer.
 */
    private List<String> fetchSkillsForFreelancer(Long freelancerId) {
        return freelancerSkillMappingService.getSkillsForFreelancer(freelancerId);
    }

}
