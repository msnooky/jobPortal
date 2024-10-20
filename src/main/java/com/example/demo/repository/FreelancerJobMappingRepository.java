package com.example.demo.repository;

import com.example.demo.models.FreelancerJobMapping;
import com.example.demo.models.FreelancerSkillMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FreelancerJobMappingRepository extends JpaRepository<FreelancerJobMapping, Long> {

    List<FreelancerJobMapping> findByJobId(Long jobId);
}