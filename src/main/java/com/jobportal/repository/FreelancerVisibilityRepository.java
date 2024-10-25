package com.jobportal.repository;

import com.jobportal.models.FreelancerVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreelancerVisibilityRepository extends JpaRepository<FreelancerVisibility, Long> {
    Optional<FreelancerVisibility> findByFreelancerId(Long id);
}