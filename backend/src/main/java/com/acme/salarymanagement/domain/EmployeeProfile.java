package com.acme.salarymanagement.domain;

import java.time.LocalDate;

public record EmployeeProfile(
        String employeeIdentifier,
        String fullName,
        String workEmail,
        String employmentStatus,
        String employmentType,
        LocalDate hireDate,
        LocalDate terminationDate,
        String country,
        String location,
        String legalEntity,
        String department,
        String businessUnit,
        String jobTitle,
        String jobFamily,
        String jobLevel,
        String managerIdentifier,
        String costCenter,
        Long employeeId) {
}