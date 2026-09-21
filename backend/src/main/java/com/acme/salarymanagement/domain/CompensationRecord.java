package com.acme.salarymanagement.domain;

import java.time.LocalDate;

public record CompensationRecord(
        long employeeId,
        CompensationType compensationType,
        long amountMinorUnits,
        String currencyCode,
        String payFrequency,
        LocalDate effectiveFrom,
        LocalDate effectiveUntil,
        String reason,
        ApprovalStatus status) {
}