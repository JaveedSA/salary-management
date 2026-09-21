package com.acme.salarymanagement.reporting;

public record CompensationAggregateMetric(
        String currencyCode,
        int employeeCount,
        boolean suppressed,
        String suppressionReason,
        Long totalMinorUnits,
        Double averageMinorUnits,
        Long medianMinorUnits,
        Long minimumMinorUnits,
        Long maximumMinorUnits,
        long periodChangeMinorUnits,
        String selectedCompensationTypes,
        String dateBasis) {

    public static CompensationAggregateMetric suppressed(String currencyCode, int employeeCount,
            String selectedCompensationTypes, String dateBasis, String reason) {
        return new CompensationAggregateMetric(currencyCode, employeeCount, true, reason, null, null, null, null,
                null, 0, selectedCompensationTypes, dateBasis);
    }
}
