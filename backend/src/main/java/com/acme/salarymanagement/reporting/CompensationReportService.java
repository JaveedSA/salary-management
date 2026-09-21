package com.acme.salarymanagement.reporting;

import java.time.LocalDate;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.acme.salarymanagement.compensation.CompensationEntity;
import com.acme.salarymanagement.compensation.CompensationRepository;
import com.acme.salarymanagement.domain.CompensationType;
import com.acme.salarymanagement.employee.EmployeeEntity;
import com.acme.salarymanagement.employee.EmployeeRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompensationReportService {

    private final EmployeeRepository employeeRepository;
    private final CompensationRepository compensationRepository;
    private final Map<String, Double> fxRatesToUsd;
    private final String fxRateSource;
    private final LocalDate fxRateDate;
    private final int privacyThreshold;

    public CompensationReportService(EmployeeRepository employeeRepository,
            CompensationRepository compensationRepository) {
        this.employeeRepository = employeeRepository;
        this.compensationRepository = compensationRepository;
        this.fxRatesToUsd = Map.of("USD", 1.0, "GBP", 1.27, "CAD", 0.74, "INR", 0.012, "JPY", 0.0067);
        this.fxRateSource = "configured-initial-rates";
        this.fxRateDate = LocalDate.of(2026, 1, 1);
        this.privacyThreshold = 5;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canViewReports(authentication)")
    public List<CompensationReportEntry> filter(CompensationReportFilter filter) {
        validateDates(filter);
        return compensationRepository.findAll().stream()
                .map(compensation -> entryFor(compensation, employeeRepository.findById(compensation.getEmployeeId()).orElse(null)))
                .filter(entry -> entry != null && matches(entry, filter))
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canViewReports(authentication)")
    public List<CompensationMetric> metrics(CompensationReportFilter filter) {
        if (filter.getCompensationType() == null || filter.getCompensationType().isBlank()) {
            throw new IllegalArgumentException("Select at least one compensation type for metrics");
        }
        List<CompensationReportEntry> entries = filter(filter);
        if (entries.isEmpty()) {
            return List.of();
        }
        String selectedTypes = filter.getCompensationType();
        return entries.stream().collect(Collectors.groupingBy(CompensationReportEntry::currencyCode,
                LinkedHashMap::new, Collectors.toList())).entrySet().stream()
                .map(group -> metric(group.getKey(), group.getValue(), selectedTypes, filter))
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canViewReports(authentication)")
    public NormalizedCompensationMetric normalizedMetrics(CompensationReportFilter filter) {
        if (filter.getReportingCurrency() == null || filter.getReportingCurrency().isBlank()) {
            throw new IllegalArgumentException("Reporting currency is required for currency normalization");
        }
        if (filter.getCompensationType() == null || filter.getCompensationType().isBlank()) {
            throw new IllegalArgumentException("Select at least one compensation type for metrics");
        }
        String target = filter.getReportingCurrency().trim().toUpperCase(Locale.ROOT);
        List<CompensationReportEntry> entries = filter(filter);
        if (entries.isEmpty()) {
            return new NormalizedCompensationMetric(target, 0, 0, 0, 0, 0, 0, 0,
                    filter.getCompensationType(), dateBasis(filter), Map.of(), fxRateSource, fxRateDate);
        }
        requireRate(target);
        Map<String, Long> nativeTotals = entries.stream().collect(Collectors.groupingBy(
                CompensationReportEntry::currencyCode, LinkedHashMap::new,
                Collectors.summingLong(CompensationReportEntry::amountMinorUnits)));
        List<Long> converted = entries.stream().map(entry -> convert(entry.amountMinorUnits(), entry.currencyCode(), target))
                .sorted().toList();
        long total = converted.stream().mapToLong(Long::longValue).sum();
        long median = converted.get(converted.size() / 2);
        if (converted.size() % 2 == 0) {
            median = (converted.get(converted.size() / 2 - 1) + converted.get(converted.size() / 2)) / 2;
        }
        long periodChange = periodChange(entries, target);
        return new NormalizedCompensationMetric(target,
                (int) entries.stream().map(CompensationReportEntry::employeeIdentifier).distinct().count(), total,
                (double) total / converted.size(), median, converted.get(0), converted.get(converted.size() - 1),
                periodChange, filter.getCompensationType(), dateBasis(filter), nativeTotals, fxRateSource, fxRateDate);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canViewReports(authentication)")
    public List<CompensationAggregateMetric> aggregateMetrics(CompensationReportFilter filter) {
        if (filter.getCompensationType() == null || filter.getCompensationType().isBlank()) {
            throw new IllegalArgumentException("Select at least one compensation type for metrics");
        }
        List<CompensationReportEntry> entries = filter(filter);
        String dateBasis = dateBasis(filter);
        return entries.stream().collect(Collectors.groupingBy(CompensationReportEntry::currencyCode,
                LinkedHashMap::new, Collectors.toList())).entrySet().stream().map(group -> {
                    List<CompensationReportEntry> values = group.getValue();
                    int employeeCount = (int) values.stream().map(CompensationReportEntry::employeeIdentifier)
                            .distinct().count();
                    if (employeeCount < privacyThreshold) {
                        return CompensationAggregateMetric.suppressed(group.getKey(), employeeCount,
                                filter.getCompensationType(), dateBasis,
                                "Aggregate restricted because the employee population is below " + privacyThreshold);
                    }
                    List<Long> amounts = values.stream().map(CompensationReportEntry::amountMinorUnits).sorted().toList();
                    long total = amounts.stream().mapToLong(Long::longValue).sum();
                    long median = amounts.get(amounts.size() / 2);
                    if (amounts.size() % 2 == 0) {
                        median = (amounts.get(amounts.size() / 2 - 1) + amounts.get(amounts.size() / 2)) / 2;
                    }
                    return new CompensationAggregateMetric(group.getKey(), employeeCount, false, null, total,
                            (double) total / amounts.size(), median, amounts.get(0), amounts.get(amounts.size() - 1),
                            0, filter.getCompensationType(), dateBasis);
                }).toList();
    }

    private long periodChange(List<CompensationReportEntry> entries, String target) {
        return entries.stream().collect(Collectors.groupingBy(
                entry -> entry.employeeIdentifier() + "|" + entry.compensationType())).values().stream()
                .mapToLong(period -> {
                    List<CompensationReportEntry> ordered = new ArrayList<>(period);
                    ordered.sort(Comparator.comparing(CompensationReportEntry::effectiveFrom));
                    return convert(ordered.get(ordered.size() - 1).amountMinorUnits(),
                            ordered.get(ordered.size() - 1).currencyCode(), target)
                            - convert(ordered.get(0).amountMinorUnits(), ordered.get(0).currencyCode(), target);
                }).sum();
    }

    private long convert(long amount, String source, String target) {
        requireRate(source);
        requireRate(target);
        return Math.round(amount * fxRatesToUsd.get(source) / fxRatesToUsd.get(target));
    }

    private void requireRate(String currency) {
        if (!fxRatesToUsd.containsKey(currency)) {
            throw new IllegalArgumentException("No FX rate is configured for currency " + currency
                    + " as of " + fxRateDate + "; source: " + fxRateSource);
        }
    }

    private static String dateBasis(CompensationReportFilter filter) {
        return String.valueOf(filter.getEffectiveFrom()) + " to " + String.valueOf(filter.getEffectiveUntil());
    }

    private static CompensationMetric metric(String currency, List<CompensationReportEntry> entries,
            String selectedTypes, CompensationReportFilter filter) {
        List<Long> amounts = entries.stream().map(CompensationReportEntry::amountMinorUnits).sorted().toList();
        long total = amounts.stream().mapToLong(Long::longValue).sum();
        long median = amounts.get(amounts.size() / 2);
        if (amounts.size() % 2 == 0) {
            median = (amounts.get(amounts.size() / 2 - 1) + amounts.get(amounts.size() / 2)) / 2;
        }
        Map<String, List<CompensationReportEntry>> periods = entries.stream().collect(Collectors.groupingBy(
                entry -> entry.employeeIdentifier() + "|" + entry.compensationType()));
        long periodChange = periods.values().stream().mapToLong(period -> {
            List<CompensationReportEntry> ordered = new ArrayList<>(period);
            ordered.sort(Comparator.comparing(CompensationReportEntry::effectiveFrom));
            return ordered.get(ordered.size() - 1).amountMinorUnits() - ordered.get(0).amountMinorUnits();
        }).sum();
        String dateBasis = String.valueOf(filter.getEffectiveFrom()) + " to " + String.valueOf(filter.getEffectiveUntil());
        return new CompensationMetric(currency, (int) entries.stream().map(CompensationReportEntry::employeeIdentifier)
                .distinct().count(), total, (double) total / amounts.size(), median, amounts.get(0),
                amounts.get(amounts.size() - 1), periodChange, selectedTypes, dateBasis);
    }

    private static CompensationReportEntry entryFor(CompensationEntity compensation, EmployeeEntity employee) {
        if (employee == null) return null;
        var profile = employee.toProfile();
        return new CompensationReportEntry(profile.employeeIdentifier(), profile.country(), profile.location(),
                profile.legalEntity(), profile.department(), profile.businessUnit(), profile.jobFamily(),
                profile.jobLevel(), profile.employmentStatus(), profile.employmentType(),
                compensation.getCompensationType(), compensation.getAmountMinorUnits(), compensation.getCurrencyCode(),
                compensation.getPayFrequency(), compensation.getEffectiveFrom(), compensation.getEffectiveUntil());
    }

    private static boolean matches(CompensationReportEntry entry, CompensationReportFilter filter) {
        return matchesText(entry.country(), filter.getCountry())
                && matchesText(entry.location(), filter.getLocation())
                && matchesText(entry.legalEntity(), filter.getLegalEntity())
                && matchesText(entry.department(), filter.getDepartment())
                && matchesText(entry.businessUnit(), filter.getBusinessUnit())
                && matchesText(entry.jobFamily(), filter.getJobFamily())
                && matchesText(entry.jobLevel(), filter.getJobLevel())
                && matchesText(entry.employmentStatus(), filter.getEmploymentStatus())
                && matchesText(entry.employmentType(), filter.getEmploymentType())
                && matchesText(entry.currencyCode(), filter.getCurrency())
                && matchesType(entry.compensationType(), filter.getCompensationType())
                && overlaps(entry.effectiveFrom(), entry.effectiveUntil(), filter.getEffectiveFrom(), filter.getEffectiveUntil());
    }

    private static boolean matchesText(String actual, String expected) {
        return expected == null || expected.isBlank() || actual.equalsIgnoreCase(expected.trim());
    }

    private static boolean matchesType(CompensationType actual, String expected) {
        if (expected == null || expected.isBlank()) return true;
        return java.util.Arrays.stream(expected.split(","))
                .map(value -> value.trim().toUpperCase(Locale.ROOT))
                .anyMatch(value -> value.equals(actual.name()));
    }

    private static boolean overlaps(LocalDate recordFrom, LocalDate recordUntil, LocalDate filterFrom,
            LocalDate filterUntil) {
        return (filterFrom == null || recordUntil == null || !recordUntil.isBefore(filterFrom))
                && (filterUntil == null || !recordFrom.isAfter(filterUntil));
    }

    private static void validateDates(CompensationReportFilter filter) {
        if (filter.getEffectiveFrom() != null && filter.getEffectiveUntil() != null
                && filter.getEffectiveUntil().isBefore(filter.getEffectiveFrom())) {
            throw new IllegalArgumentException("Effective date end cannot precede effective date start");
        }
    }
}