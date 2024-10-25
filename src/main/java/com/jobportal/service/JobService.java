package com.jobportal.service;

import com.jobportal.dto.FreelancerDto;
import com.jobportal.dto.JobDto;
import com.jobportal.dto.SearchDto;
import com.jobportal.models.*;
import com.jobportal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private UserService userService;

    @Autowired
    FreelancerVisibilityService freelancerVisibilityService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private JobSkillMappingRepository jobSkillMappingRepository;

    @Autowired
    private FreelancerJobMappingRepository freelancerJobMappingRepository;

    public void createJob(Job job, List<String> skills) {
        jobRepository.save(job);
        List<JobSkillMapping> jobSkillMappings = skills.stream()
                .map(skill -> {
                    JobSkillMapping mapping = new JobSkillMapping();
                    mapping.setJobId(job.getJobId());
                    mapping.setSkill(skill);
                    return mapping;
                })
                .collect(Collectors.toList());
        jobSkillMappingRepository.saveAll(jobSkillMappings);
    }

    public List<JobDto> searchJobs(String username, SearchDto searchDto) {
        User user = userService.findUserByUsername(username);
        if (user.getRole().equalsIgnoreCase("Freelancer")) {
            List<Job> jobs = jobRepository.findAll();
            if (!ObjectUtils.isEmpty(searchDto.getSkills())) {
                jobs = filterJobsWithSkills(searchDto.getSkills(), jobs);
            }
            if (searchDto.getLocation().isPresent()) {
                jobs = filterJobsWithLocation(searchDto.getLocation().get(), jobs);
            }
            if (searchDto.getMinSalary().isPresent() || searchDto.getMaxSalary().isPresent()) {
                jobs = filterJobsInSalaryRange(searchDto, jobs);
            }
            return jobs.stream().map(job -> new JobDto(job.getJobId(),
                    job.getTitle(),
                    job.getDescription(),
                    job.getLocation(),
                    job.getSalary(),
                    job.getTags(),
                    getJobSkills(job.getJobId()))).toList();
        } else {
            throw new RuntimeException("Unauthorized to search for jobs");
        }
    }

    private List<Job> filterJobsWithSkills(List<String> skills, List<Job> jobs) {
        List<Long> jobIds = jobSkillMappingRepository.findAllBySkillIn(skills);
        return jobRepository.findAllByJobIdIn(jobIds).stream()
                .filter(job -> jobs.stream().anyMatch(newJob -> newJob.getJobId()
                        .equals(job.getJobId()))).collect(Collectors.toList());
    }

    private List<Job> filterJobsWithLocation(String location, List<Job> jobs) {
        return jobRepository.findByLocation(location).stream()
                .filter(job -> jobs.stream().anyMatch(newJob -> newJob.getJobId()
                        .equals(job.getJobId()))).collect(Collectors.toList());
    }

    private List<Job> filterJobsInSalaryRange(SearchDto searchDto, List<Job> jobs) {
        Long minSalary = searchDto.getMinSalary().orElse(0L);
        ;
        Long maxSalary = searchDto.getMaxSalary().orElse(Long.MAX_VALUE);
        return jobRepository.findBySalaryBetween(minSalary, maxSalary).stream()
                .filter(job -> jobs.stream().anyMatch(newJob -> newJob.getJobId()
                        .equals(job.getJobId()))).collect(Collectors.toList());
    }

    public List<FreelancerDto> getApplicants(String username, Long jobId) {
        User user = userService.findUserByUsername(username);
        if (user.getRole().equalsIgnoreCase("Employer")) {
            Optional<Job> job = jobRepository.findById(jobId);
            Optional<Employer> employer = employerRepository.findById(job.get().getEmployerId());
            if (employer.get().getUserId().equals(user.getId())) {
                List<FreelancerJobMapping> freelancerJobMappings = freelancerJobMappingRepository.findByJobId(jobId);
                if (!freelancerJobMappings.isEmpty()) {
                    List<Long> freelancerIds = freelancerJobMappings.stream()
                            .map(FreelancerJobMapping::getFreelancerId)
                            .collect(Collectors.toList());

                    List<Freelancer> freelancers = findAllFreelancersByIds(freelancerIds);
                    return freelancerVisibilityService.getVisibilityOfFreelancers(freelancers);
                }
                return new ArrayList<>();
            } else {
                throw new RuntimeException("Unauthorized employer");
            }
        } else {
            throw new RuntimeException("Unauthorized as an employer");
        }
    }

    private List<Freelancer> findAllFreelancersByIds(List<Long> freelancerIds) {
        return freelancerRepository.findAllById(freelancerIds);
    }

    public String deleteJob(Employer employer, Long id) {
        Optional<Job> job = jobRepository.findById(id);
        if (Objects.equals(employer.getEmployerId(), job.get().getEmployerId())) {
            jobRepository.deleteById(id);
            jobSkillMappingRepository.deleteByJobId(id);
            return "Successfully deleted";
        } else {
            throw new RuntimeException("Not authorized");
        }
    }

    public List<Job> getJobsByEmployerId(Long employerId) {
        return jobRepository.findAllByEmployerId(employerId);
    }

    public List<JobDto> getAllJobs(String username) {
        User user = userService.findUserByUsername(username);
        if (user.getRole().equalsIgnoreCase("Freelancer")) {
            List<Job> jobs = jobRepository.findAll();
            return jobs.stream().map(job -> new JobDto(job.getJobId(),
                    job.getTitle(),
                    job.getDescription(),
                    job.getLocation(),
                    job.getSalary(),
                    job.getTags(),
                    getJobSkills(job.getJobId()))).collect(Collectors.toList());
        } else {
            throw new RuntimeException("Unauthorized user");
        }
    }

    public List<String> getJobSkills(Long jobId) {
        return jobSkillMappingRepository.findByJobId(jobId)
                .stream()
                .map(JobSkillMapping::getSkill)
                .collect(Collectors.toList());
    }

    Optional<Job> findJob(Long jobId) {
        return jobRepository.findById(jobId);
    }
}
