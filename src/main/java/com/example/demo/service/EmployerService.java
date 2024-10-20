package com.example.demo.service;

import com.example.demo.dto.EmployerDto;
import com.example.demo.dto.JobDto;
import com.example.demo.models.Employer;
import com.example.demo.models.Job;
import com.example.demo.models.User;
import com.example.demo.repository.EmployerRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private JobService jobService;

    public ResponseEntity<String> createEmployer(EmployerDto employer, String userName) {
        try {
            User user = userRepository.findByName(userName).get();
            Optional<Employer> employer2 = employerRepository.findByUserId(user.getId());
            if (employer2.isEmpty()) {
                Employer employer1 = Employer.builder()
                        .companyName(employer.getCompanyName())
                        .userId(user.getId()).build();
                employerRepository.save(employer1);
            }
            return ResponseEntity.ok("SUCCESS");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());

        }
    }

    public ResponseEntity<String> updateEmployer(EmployerDto employerDto, String userName) {
        try {
            User user = userRepository.findByName(userName).get();
            Optional<Employer> employer2 = employerRepository.findByUserId(user.getId());
            if (employer2.isPresent()) {
                employer2.get().setCompanyName(employerDto.getCompanyName());
                employerRepository.save(employer2.get());
                return ResponseEntity.ok("SUCCESS");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        return ResponseEntity.internalServerError().body("Employer not found");
    }

    public List<JobDto> getEmployerJobs(String username) {
        User user = userRepository.findByName(username).get();
        Optional<Employer> employer = employerRepository.findByUserId(user.getId());
        if (employer.isPresent()) {
            List<Job> jobs = jobService.getJobsByEmployerId(employer.get().getEmployerId());
            return jobs.stream().map(job -> new JobDto(job.getJobId(),
                    job.getTitle(),
                    job.getDescription(),
                    job.getLocation(),
                    job.getSalary(),
                    job.getTags())
            ).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public ResponseEntity<String> deleteEmployer(String username) {
        try {
            User user = userRepository.findByName(username).get();
            userRepository.deleteById(user.getId());
            Optional<Employer> employer2 = employerRepository.findByUserId(user.getId());
            employer2.ifPresent(employer -> employerRepository.deleteById(employer.getEmployerId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        return ResponseEntity.ok("SUCCESS");

    }

    public ResponseEntity<String> postJobs(String username, JobDto jobDto) {
        try {
            User user = userRepository.findByName(username).get();
            Optional<Employer> employer = employerRepository.findByUserId(user.getId());
            Job job = Job.builder()
                    .employerId(employer.get().getEmployerId())
                    .tags(jobDto.getTags())
                    .description(jobDto.getDescription())
                    .location(jobDto.getLocation())
                    .title(jobDto.getTitle())
                    .salary(jobDto.getSalary()).build();
            jobService.createJob(job);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteJob(Long jobId) {
        try {
            jobService.deleteJob(jobId);
            return ResponseEntity.ok("SUCCESS");

        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }
}
