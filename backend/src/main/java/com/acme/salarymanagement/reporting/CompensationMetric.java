package com.acme.salarymanagement.reporting;

public record CompensationMetric(
        String currencyCode,
        int employeeCount,
        long totalMinorUnits,
        double averageMinorUnits,
        long medianMinorUnits,
        long minimumMinorUnits,
        long maximumMinorUnits,
        long periodChangeMinorUnits,
        String selectedCompensationTypes,
        String dateBasis) {
}
