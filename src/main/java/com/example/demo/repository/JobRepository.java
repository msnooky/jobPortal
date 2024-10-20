package com.example.demo.repository;

import com.example.demo.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByLocation(String location);
    List<Job> findBySalaryBetween(Long minSalary, Long maxSalary);
    List<Job> findAllByJobIdIn(List<Long> jobIds);
    List<Job> findAllByEmployerId(Long employerId);
}
