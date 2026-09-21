package com.acme.salarymanagement.reporting;

import java.time.LocalDate;
import java.util.Map;

public record NormalizedCompensationMetric(
        String reportingCurrency,
        int employeeCount,
        long totalMinorUnits,
        double averageMinorUnits,
        long medianMinorUnits,
        long minimumMinorUnits,
        long maximumMinorUnits,
        long periodChangeMinorUnits,
        String selectedCompensationTypes,
        String dateBasis,
        Map<String, Long> nativeTotalsByCurrency,
        String fxRateSource,
        LocalDate fxRateDate) {
}
