package com.jobportal.repository;

import com.jobportal.models.EmployerEmployeeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EmployerEmployeeMappingRepository extends JpaRepository<EmployerEmployeeMapping, Long> {
    @Modifying
    @Transactional
    @Query("SELECT e.employeeId FROM EmployerEmployeeMapping e WHERE e.employerId = :employerId")
    List<Long> findEmployeeIdByEmployerId(@Param("employerId") Long employerId);

    boolean existsByEmployerIdAndEmployeeId(Long employerId, Long employeeId);
}