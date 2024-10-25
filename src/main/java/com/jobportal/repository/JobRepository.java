package com.jobportal.repository;

import com.jobportal.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByLocation(String location);
    List<Job> findBySalaryBetween(Long minSalary, Long maxSalary);
    List<Job> findAllByJobIdIn(List<Long> jobIds);
    List<Job> findAllByEmployerId(Long employerId);
}
