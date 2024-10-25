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

    /**
     * Creates a new job with the given details and skills.
     *
     * @param job    The job object containing job details.
     * @param skills A list of skills associated with the job.
     */
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

    /**
     * Searches for jobs based on the provided search criteria.
     *
     * @param username   The username of the user performing the search.
     * @param searchDto The search criteria object.
     * @return A list of JobDto objects representing the matching jobs.
     */
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

    /**
     * Filters jobs based on the provided skills.
     *
     * @param skills The list of skills to filter by.
     * @param jobs   The initial list of jobs to filter.
     * @return A list of jobs that match the provided skills.
     */
    private List<Job> filterJobsWithSkills(List<String> skills, List<Job> jobs) {
        List<Long> jobIds = jobSkillMappingRepository.findAllBySkillIn(skills);
        return jobRepository.findAllByJobIdIn(jobIds).stream()
                .filter(job -> jobs.stream().anyMatch(newJob -> newJob.getJobId()
                        .equals(job.getJobId()))).collect(Collectors.toList());
    }

    /**
     * Filters jobs based on the provided location.
     *
     * @param location The location to filter by.
     * @param jobs     The initial list of jobs to filter.
     * @return A list of jobs that match the provided location.
     */
    private List<Job> filterJobsWithLocation(String location, List<Job> jobs) {
        return jobRepository.findByLocation(location).stream()
                .filter(job -> jobs.stream().anyMatch(newJob -> newJob.getJobId()
                        .equals(job.getJobId()))).collect(Collectors.toList());
    }

    /**
     * Filters jobs within the specified salary range.
     *
     * @param searchDto The search criteria object containing salary range.
     * @param jobs      The initial list of jobs to filter.
     * @return A list of jobs that fall within the specified salary range.
     */
    private List<Job> filterJobsInSalaryRange(SearchDto searchDto, List<Job> jobs) {
        Long minSalary = searchDto.getMinSalary().orElse(0L);
        ;
        Long maxSalary = searchDto.getMaxSalary().orElse(Long.MAX_VALUE);
        return jobRepository.findBySalaryBetween(minSalary, maxSalary).stream()
                .filter(job -> jobs.stream().anyMatch(newJob -> newJob.getJobId()
                        .equals(job.getJobId()))).collect(Collectors.toList());
    }

    /**
     * Retrieves a list of freelancer DTOs representing the applicants for a specific job.
     *
     * @param username The username of the user requesting the applicants.
     * @param jobId    The ID of the job for which to retrieve applicants.
     * @return A list of freelancer DTOs representing the applicants for the job.
     * @throws RuntimeException If the user is not authorized as an employer or if the employer is not authorized to access the job.
     */
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

    /**
     * Finds all freelancers by their IDs.
     *
     * @param freelancerIds A list of freelancer IDs.
     * @return A list of freelancers matching the provided IDs.
     */
    private List<Freelancer> findAllFreelancersByIds(List<Long> freelancerIds) {
        return freelancerRepository.findAllById(freelancerIds);
    }

    /**
     * Deletes a job.
     *
     * @param employer The employer attempting to delete the job.
     * @param id       The ID of the job to delete.
     * @return A message indicating the success or failure of the deletion.
     * @throws RuntimeException If the employer is not authorized to delete the job.
     */
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

    /**
     * Retrieves all jobs associated with a specific employer ID.
     *
     * @param employerId The ID of the employer.
     * @return A list of jobs associated with the employer ID.
     */
    public List<Job> getJobsByEmployerId(Long employerId) {
        return jobRepository.findAllByEmployerId(employerId);
    }

    /**
     * Retrieves all jobs as DTOs, accessible only to freelancers.
     *
     * @param username The username of the user requesting the jobs.
     * @return A list of job DTOs representing all available jobs.
     * @throws RuntimeException If the user is not authorized as a freelancer.
     */
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

    /**
     * Retrieves a list of skills associated with a specific job ID.
     *
     * @param jobId The ID of the job.
     * @return A list of skills associated with the job ID.
     */
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
