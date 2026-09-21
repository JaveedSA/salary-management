package com.acme.salarymanagement.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    Optional<EmployeeEntity> findByEmployeeIdentifier(String employeeIdentifier);

    boolean existsByEmployeeIdentifierAndIdNot(String employeeIdentifier, long id);

    List<EmployeeEntity> findByFullNameContainingIgnoreCaseOrEmployeeIdentifierContainingIgnoreCase(
            String name, String employeeIdentifier);
}