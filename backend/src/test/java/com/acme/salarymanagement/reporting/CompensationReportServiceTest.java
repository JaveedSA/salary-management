package com.acme.salarymanagement.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.acme.salarymanagement.compensation.CompensationEntity;
import com.acme.salarymanagement.compensation.CompensationRepository;
import com.acme.salarymanagement.domain.ApprovalStatus;
import com.acme.salarymanagement.domain.CompensationRecord;
import com.acme.salarymanagement.domain.CompensationType;
import com.acme.salarymanagement.domain.EmployeeProfile;
import com.acme.salarymanagement.employee.EmployeeEntity;
import com.acme.salarymanagement.employee.EmployeeRepository;

class CompensationReportServiceTest {

    private EmployeeRepository employeeRepository;
    private CompensationRepository compensationRepository;
    private CompensationReportService service;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        compensationRepository = mock(CompensationRepository.class);
        service = new CompensationReportService(employeeRepository, compensationRepository);
    }

    @Test
    void filtersByPopulationAndSelectedCompensationTypes() {
        CompensationEntity salary = compensation(1, CompensationType.BASE_SALARY, 100_000, "USD");
        CompensationEntity bonus = compensation(2, CompensationType.BONUS, 20_000, "USD");
        when(compensationRepository.findAll()).thenReturn(List.of(salary, bonus));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1)));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee(2)));

        CompensationReportFilter filter = filter("BASE_SALARY", null, null);

        assertEquals(1, service.filter(filter).size());
        assertEquals(1, service.metrics(filter).get(0).employeeCount());
    }

    @Test
    void rejectsInvalidDateRange() {
        CompensationReportFilter filter = filter("BASE_SALARY", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1));
        when(compensationRepository.findAll()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> service.filter(filter));
    }

    @Test
    void normalizesMultipleCurrenciesUsingConfiguredRates() {
        CompensationEntity usd = compensation(1, CompensationType.BASE_SALARY, 100_00, "USD");
        CompensationEntity gbp = compensation(2, CompensationType.BASE_SALARY, 100_00, "GBP");
        when(compensationRepository.findAll()).thenReturn(List.of(usd, gbp));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1)));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee(2)));

        CompensationReportFilter filter = filter("BASE_SALARY", null, null);
        filter.setReportingCurrency("USD");
        NormalizedCompensationMetric result = service.normalizedMetrics(filter);

        assertEquals("USD", result.reportingCurrency());
        assertEquals(22_700L, result.totalMinorUnits());
        assertEquals(2, result.nativeTotalsByCurrency().size());
    }

    @Test
    void reportsMissingFxData() {
        CompensationEntity unknown = compensation(1, CompensationType.BASE_SALARY, 100_00, "CHF");
        when(compensationRepository.findAll()).thenReturn(List.of(unknown));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1)));

        CompensationReportFilter filter = filter("BASE_SALARY", null, null);
        filter.setReportingCurrency("USD");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
            () -> service.normalizedMetrics(filter));

        assertEquals(true, error.getMessage().contains("No FX rate is configured for currency CHF"));
    }

    @Test
    void suppressesAggregateMetricsBelowPrivacyThreshold() {
        List<CompensationEntity> records = IntStream.rangeClosed(1, 4)
                .mapToObj(id -> compensation(id, CompensationType.BASE_SALARY, id * 10_000, "USD")).toList();
        when(compensationRepository.findAll()).thenReturn(records);
        records.forEach(record -> when(employeeRepository.findById(record.getEmployeeId())).thenReturn(Optional.of(employee((int) record.getEmployeeId()))));

        CompensationAggregateMetric result = service.aggregateMetrics(filter("BASE_SALARY", null, null)).get(0);

        assertEquals(true, result.suppressed());
        assertEquals(null, result.totalMinorUnits());
    }

    private static CompensationReportFilter filter(String type, LocalDate from, LocalDate until) {
        CompensationReportFilter filter = new CompensationReportFilter();
        filter.setCompensationType(type);
        filter.setEffectiveFrom(from);
        filter.setEffectiveUntil(until);
        return filter;
    }

    private static CompensationEntity compensation(long employeeId, CompensationType type, long amount, String currency) {
        return new CompensationEntity(new CompensationRecord(employeeId, type, amount, currency, "MONTHLY",
                LocalDate.of(2026, 1, 1), null, "test", ApprovalStatus.APPROVED));
    }

    private static EmployeeEntity employee(int id) {
        return new EmployeeEntity(new EmployeeProfile("EMP-" + id, "Employee " + id, "employee" + id + "@example.com",
                "ACTIVE", "FULL_TIME", LocalDate.of(2020, 1, 1), null, "GB", "London", "ACME", "Engineering",
                "Technology", "Engineer", "Engineering", "P2", null, "CC-1", (long) id));
    }
}
