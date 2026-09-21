package com.acme.salarymanagement.employee;

import java.util.List;

import com.acme.salarymanagement.domain.EmployeeProfile;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canReadEmployee(authentication, #id)")
    public EmployeeProfile get(long id) {
        return repository.findById(id).orElseThrow().toProfile();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canManageEmployees(authentication)")
    public List<EmployeeProfile> search(String query) {
        return repository.findByFullNameContainingIgnoreCaseOrEmployeeIdentifierContainingIgnoreCase(query, query)
                .stream().map(EmployeeEntity::toProfile).toList();
    }

    @Transactional
    @PreAuthorize("@authorizationService.canManageEmployees(authentication)")
    public EmployeeProfile create(EmployeeProfile profile) {
        validate(profile);
        if (repository.findByEmployeeIdentifier(profile.employeeIdentifier()).isPresent()) {
            throw new IllegalArgumentException("Employee identifier already exists");
        }
        return repository.save(new EmployeeEntity(profile)).toProfile();
    }

    @Transactional
    @PreAuthorize("@authorizationService.canManageEmployees(authentication)")
    public EmployeeProfile update(long id, EmployeeProfile profile) {
        validate(profile);
        EmployeeEntity employee = repository.findById(id).orElseThrow();
        if (repository.existsByEmployeeIdentifierAndIdNot(profile.employeeIdentifier(), id)) {
            throw new IllegalArgumentException("Employee identifier already exists");
        }
        employee.update(profile);
        return repository.save(employee).toProfile();
    }

    private static void validate(EmployeeProfile profile) {
        if (profile == null || isBlank(profile.employeeIdentifier()) || isBlank(profile.fullName())
                || isBlank(profile.workEmail()) || !profile.workEmail().contains("@")
                || isBlank(profile.employmentStatus()) || isBlank(profile.employmentType())
                || profile.hireDate() == null || isBlank(profile.country())) {
            throw new IllegalArgumentException("Invalid employee profile: required identity and employment fields are missing");
        }
        if (profile.terminationDate() != null && profile.terminationDate().isBefore(profile.hireDate())) {
            throw new IllegalArgumentException("Termination date cannot precede hire date");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}