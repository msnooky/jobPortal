package com.jobportal.repository;

import com.jobportal.models.FreelancerSkillMapping;
import com.jobportal.models.JobSkillMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface JobSkillMappingRepository extends JpaRepository<JobSkillMapping, Long> {

    @Query(value = "Select job_id from job_skill_mapping where skill IN ?1", nativeQuery = true)
    List<Long> findAllBySkillIn(List<String> skills);

    List<JobSkillMapping> findByJobId(Long jobId);

    void deleteByJobId(Long jobId);
}