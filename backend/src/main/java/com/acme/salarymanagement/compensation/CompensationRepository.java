package com.acme.salarymanagement.compensation;

import java.time.LocalDate;
import java.util.List;

import com.acme.salarymanagement.domain.CompensationType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompensationRepository extends JpaRepository<CompensationEntity, Long> {

    List<CompensationEntity> findByEmployeeIdOrderByEffectiveFromDesc(long employeeId);

    @Query("select c from CompensationEntity c where c.employeeId = :employeeId and c.compensationType = :type "
            + "and c.effectiveFrom <= coalesce(:effectiveUntil, c.effectiveFrom) "
            + "and coalesce(c.effectiveUntil, :effectiveFrom) >= :effectiveFrom")
    List<CompensationEntity> findOverlapping(@Param("employeeId") long employeeId,
            @Param("type") CompensationType type,
            @Param("effectiveFrom") LocalDate effectiveFrom,
            @Param("effectiveUntil") LocalDate effectiveUntil);
}