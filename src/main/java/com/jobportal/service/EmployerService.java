package com.jobportal.service;

import com.jobportal.dto.EmployerDto;
import com.jobportal.dto.FreelancerDto;
import com.jobportal.dto.JobDto;
import com.jobportal.models.*;
import com.jobportal.repository.EmployerEmployeeMappingRepository;
import com.jobportal.repository.EmployerRepository;
import com.jobportal.repository.FreelancerRepository;
import com.jobportal.repository.FreelancerVisibilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployerService {

    @Autowired
    private FreelancerService freelancerService;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    FreelancerVisibilityService freelancerVisibilityService;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private EmployerEmployeeMappingRepository employerEmployeeMappingRepository;

    /**
     * Creates a new employer.
     *
     * @param employerDto The employer data transfer object.
     * @param username The username of the user creating the employer.
     * @return A message indicating whether the employer was created successfully.
     */
    public String createEmployer(EmployerDto employerDto, String username) {
        User user = userService.findUserByUsername(username);
        if (user.getRole().equalsIgnoreCase("Employer")) {
            Optional<Employer> newEmployer = employerRepository.findByUserId(user.getId());
            if (newEmployer.isEmpty()) {
                Employer employer = Employer.builder()
                        .companyName(employerDto.getCompanyName())
                        .userId(user.getId()).build();
                employerRepository.save(employer);
                return "Employer created successfully";
            } else {
                return "Employer already exists";
            }
        } else {
            throw new RuntimeException("Not authorized as an Employer");
        }
    }

    /**
     * Updates an existing employer.
     *
     * @param employerDto The employer data transfer object.
     * @param username The username of the user updating the employer.
     * @return A message indicating whether the employer was updated successfully.
     */
    public String updateEmployer(EmployerDto employerDto, String username) {
        User user = userService.findUserByUsername(username);
        Employer newEmployer = getEmployerByEmployerId(user.getId());
        newEmployer.setCompanyName(employerDto.getCompanyName());
        employerRepository.save(newEmployer);
        return "Successfully Updated";

    }

    /**
     * Gets a list of jobs posted by an employer.
     *
     * @param username The username of the employer.
     * @return A list of job data transfer objects.
     */
    public List<JobDto> getEmployerJobs(String username) {
        User user = userService.findUserByUsername(username);
        if (user.getRole().equalsIgnoreCase("Employer")) {
            Employer employer = getEmployerByEmployerId(user.getId());
            List<Job> jobs = jobService.getJobsByEmployerId(employer.getEmployerId());
            return jobs.stream().map(job -> new JobDto(job.getJobId(),
                    job.getTitle(),
                    job.getDescription(),
                    job.getLocation(),
                    job.getSalary(),
                    job.getTags(),
                    jobService.getJobSkills(job.getJobId()))
            ).collect(Collectors.toList());
        } else {
            throw new RuntimeException("Unauthorized as an employer");
        }
    }

    /**
     * Deletes an employer.
     *
     * @param username The username of the employer to delete.
     * @return A message indicating whether the employer was deleted successfully.
     */
    public String deleteEmployer(String username) {
        User user = userService.findUserByUsername(username);
        userService.deleteEmployer(user.getId());
        Employer newEmployer = getEmployerByEmployerId(user.getId());
        employerRepository.deleteById(newEmployer.getEmployerId());
        return "Successfully Deleted";
    }

    /**
     * Posts a new job.
     *
     * @param username The username of the user posting the job.
     * @param jobDto The job data transfer object.
     * @return A message indicating whether the job was posted successfully.
     */
    public String postJobs(String username, JobDto jobDto) {
        User user = userService.findUserByUsername(username);
        Employer employer = getEmployerByEmployerId(user.getId());
        if (user.getRole().equalsIgnoreCase("Employer")) {
            Job job = Job.builder()
                    .employerId(employer.getEmployerId())
                    .tags(jobDto.getTags())
                    .description(jobDto.getDescription())
                    .location(jobDto.getLocation())
                    .title(jobDto.getTitle())
                    .salary(jobDto.getSalary()).build();
            jobService.createJob(job, jobDto.getSkills());
            return "Successfully Posted job";
        } else {
            throw new RuntimeException("Unauthorized as an employer");
        }
    }

    /**
     * Deletes a job.
     *
     * @param username The username of the user deleting the job.
     * @param jobId The ID of the job to delete.
     * @return A message indicating whether the job was deleted successfully.
     */
    public String deleteJob(String username, Long jobId) {
        User user = userService.findUserByUsername(username);
        Employer employer = getEmployerByEmployerId(user.getId());
        return jobService.deleteJob(employer, jobId);
    }

    public String acceptApplication(String username, Long freelancerId) {
        User user = userService.findUserByUsername(username);
        Employer employer = getEmployerByEmployerId(user.getId());
        boolean exists = employerEmployeeMappingRepository.existsByEmployerIdAndEmployeeId(employer.getEmployerId(), freelancerId);
        if (exists) {
            throw new RuntimeException("Freelancer is already added as an Employee");
        }
        EmployerEmployeeMapping mapping = EmployerEmployeeMapping.builder()
                .employerId(employer.getEmployerId())
                .employeeId(freelancerId).build();
        employerEmployeeMappingRepository.save(mapping);
        return "Freelancer added as an Employee";
    }

    public List<FreelancerDto> getEmployees(String username) {
        User user = userService.findUserByUsername(username);
        Employer employer = getEmployerByEmployerId(user.getId());
        List<Long> employeeIds = employerEmployeeMappingRepository.findEmployeeIdByEmployerId(employer.getEmployerId());
        return freelancerService.getFreelancersByIds(employeeIds);
    }

}
