package com.jobportal.repository;

import com.jobportal.models.FreelancerSkillMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FreelancerSkillMappingRepository extends JpaRepository<FreelancerSkillMapping, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE FreelancerSkillMapping fsm SET fsm.skill = :skill WHERE fsm.freelancerId = :freelancerId")
    int updateSkillByFreelancerId(@Param("freelancerId") Long freelancerId, @Param("skill") String skill);

    List<FreelancerSkillMapping> findByFreelancerId(Long freelancerId);
}