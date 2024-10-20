package com.example.demo.repository;

import com.example.demo.models.JobSkillMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobSkillMappingRepository extends JpaRepository<JobSkillMapping, Long> {
    Long findBySkill(String skill);

    @Query(value = "Select job_id from job_skill_mapping where skill IN ?1", nativeQuery = true)
    List<Long> findAllBySkillIn(List<String> skills);

}