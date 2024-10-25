package com.jobportal.repository;

import com.jobportal.models.FreelancerJobMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FreelancerJobMappingRepository extends JpaRepository<FreelancerJobMapping, Long> {

    List<FreelancerJobMapping> findByJobId(Long jobId);

    Optional<FreelancerJobMapping> findByFreelancerIdAndJobId(Long id, Long jobId);
}
