package com.example.demo.service;

import com.example.demo.dto.JobDto;
import com.example.demo.dto.SearchDto;
import com.example.demo.models.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobSkillMappingRepository jobSkillMappingRepository;

    @Autowired
    private FreelancerJobMappingRepository freelancerJobMappingRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    public Job updateJob(Long id, Job job) {
        Optional<Job> existing = jobRepository.findById(id);
        if (existing.isPresent()) {
            Job updated = existing.get();
            //updated.setTitle(job.getTitle());
            //updated.setDescription(job.getDescription());
            //updated.setLocation(job.getLocation());
            //updated.setSalaryRange(job.getSalaryRange());
            return jobRepository.save(updated);
        }
        throw new RuntimeException("Job not found");
    }

    public List<Job> searchJobs(SearchDto searchDto) {
        List<Job> jobs = jobRepository.findAll();
        if (!ObjectUtils.isEmpty(searchDto.getSkills())) {
            List<Long> jobIds = jobSkillMappingRepository.findAllBySkillIn(searchDto.getSkills());
            if (!ObjectUtils.isEmpty(jobIds)) {
                List<Job> finalJobs = jobs;
                jobs = jobRepository.findAllByJobIdIn(jobIds).stream().filter(job -> finalJobs.stream().anyMatch(job1 -> job1.getJobId().equals(job.getJobId()))).collect(Collectors.toList());
            }
        }
        if (searchDto.getLocation().isPresent()) {
            String s = searchDto.getLocation().get();
            List<Job> finalJobs = jobs;
            jobs = jobRepository.findByLocation(s).stream().filter(job -> finalJobs.stream().anyMatch(job1 -> job1.getJobId().equals(job.getJobId()))).collect(Collectors.toList());
        }
        if (searchDto.getMinSalary().isPresent() || searchDto.getMaxSalary().isPresent()) {
            Long minSalary = searchDto.getMinSalary().get();
            Long maxSalary = searchDto.getMaxSalary().get();
            List<Job> finalJobs = jobs;
            jobs = jobRepository.findBySalaryBetween(minSalary, maxSalary).stream().filter(job -> finalJobs.stream().anyMatch(job1 -> job1.getJobId().equals(job.getJobId()))).collect(Collectors.toList());
        }
        return jobs.isEmpty() ? jobRepository.findAll() : jobs.stream().toList();
    }

    public ResponseEntity<String> applyForJob(String username, Long jobId) {
        try {
            User user = userRepository.findByName(username).get();
            Optional<Job> job = jobRepository.findById(jobId);
            if (!job.isEmpty()) {
                FreelancerJobMapping mapping = FreelancerJobMapping.builder()
                        .jobId(jobId)
                        .freelancerId(user.getId()).build();
                freelancerJobMappingRepository.save(mapping);
                return ResponseEntity.ok("Successfully applied");
            } else {
                return ResponseEntity.ok("No job found with the job id: " + jobId);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());

        }
    }

    public List<Freelancer> getApplicants(String username, Long jobId) {
        User user = userRepository.findByName(username).get();
        List<FreelancerJobMapping> freelancerJobMappings = freelancerJobMappingRepository.findByJobId(jobId);
        if (!freelancerJobMappings.isEmpty()) {
            List<Long> freelancerIds = freelancerJobMappings.stream()
                    .map(FreelancerJobMapping::getFreelancerId)
                    .collect(Collectors.toList());
            return freelancerRepository.findAllById(freelancerIds);
        }
        return new ArrayList<>();
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    public List<Job> getJobsByEmployerId(Long employerId) {
        return jobRepository.findAllByEmployerId(employerId);
    }

    public List<JobDto> getAllJobs(String username) {
        List<JobDto> jobDtos = new ArrayList<>();
        User user = userRepository.findByName(username).get();
        if (user.getRole().equalsIgnoreCase("Freelancer")) {
            List<Job> jobs = jobRepository.findAll();
            jobDtos = jobs.stream().map(job -> new JobDto(job.getJobId(),
                    job.getTitle(),
                    job.getDescription(),
                    job.getLocation(),
                    job.getSalary(),
                    job.getTags())).collect(Collectors.toList());
        }
        return jobDtos;
    }
}
