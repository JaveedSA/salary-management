package com.acme.salarymanagement.reporting;

import java.time.LocalDate;

import com.acme.salarymanagement.domain.CompensationType;

public record CompensationReportEntry(
        String employeeIdentifier,
        String country,
        String location,
        String legalEntity,
        String department,
        String businessUnit,
        String jobFamily,
        String jobLevel,
        String employmentStatus,
        String employmentType,
        CompensationType compensationType,
        long amountMinorUnits,
        String currencyCode,
        String payFrequency,
        LocalDate effectiveFrom,
        LocalDate effectiveUntil) {
}
