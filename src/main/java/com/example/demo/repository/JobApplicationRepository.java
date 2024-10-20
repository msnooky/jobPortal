package com.example.demo.repository;

import com.example.demo.models.Job;
import com.example.demo.models.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
}
