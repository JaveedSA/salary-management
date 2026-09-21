package com.acme.salarymanagement.acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.acme.salarymanagement.audit.AuditEventService;
import com.acme.salarymanagement.compensation.CompensationEntity;
import com.acme.salarymanagement.compensation.CompensationRepository;
import com.acme.salarymanagement.compensation.CompensationService;
import com.acme.salarymanagement.domain.ApprovalStatus;
import com.acme.salarymanagement.domain.CompensationRecord;
import com.acme.salarymanagement.domain.CompensationType;
import com.acme.salarymanagement.domain.EmployeeProfile;
import com.acme.salarymanagement.employee.EmployeeEntity;
import com.acme.salarymanagement.employee.EmployeeRepository;
import com.acme.salarymanagement.employee.EmployeeService;
import com.acme.salarymanagement.imports.ImportBatchRepository;
import com.acme.salarymanagement.imports.ImportFailureService;
import com.acme.salarymanagement.imports.ImportService;

class CapabilityAcceptanceServiceTest {

    @Test
    void rejectsDuplicateEmployeeIdentifierWithoutSaving() {
        EmployeeRepository repository = mock(EmployeeRepository.class);
        when(repository.findByEmployeeIdentifier("EMP-001")).thenReturn(Optional.of(employee("EMP-001")));
        EmployeeService service = new EmployeeService(repository, mock(AuditEventService.class));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.create(profile("EMP-001")));

        assertEquals("Employee identifier already exists", error.getMessage());
        verify(repository, never()).save(any(EmployeeEntity.class));
    }

    @Test
    void rejectsOverlappingCompensationPeriod() {
        CompensationRepository repository = mock(CompensationRepository.class);
        when(repository.findOverlapping(1L, CompensationType.BASE_SALARY, LocalDate.of(2026, 6, 1), null))
                .thenReturn(List.of(new CompensationEntity(new CompensationRecord(1L,
                        CompensationType.BASE_SALARY, 100_000, "USD", "MONTHLY", LocalDate.of(2026, 1, 1),
                        null, "existing", ApprovalStatus.APPROVED))));
        CompensationService service = new CompensationService(repository, mock(AuditEventService.class));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.create(new CompensationRecord(1L, CompensationType.BASE_SALARY, 120_000,
                        "USD", "MONTHLY", LocalDate.of(2026, 6, 1), null, "overlap", null)));

        assertEquals("Compensation period overlaps an existing record", error.getMessage());
        verify(repository, never()).save(any(CompensationEntity.class));
    }

    @Test
    void rejectsUnsupportedImportFilesBeforeCreatingBatch() {
        ImportBatchRepository repository = mock(ImportBatchRepository.class);
        ImportService service = new ImportService(repository, mock(EmployeeRepository.class),
                mock(CompensationRepository.class), mock(ImportFailureService.class), mock(AuditEventService.class));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.stage(new MockMultipartFile("file", "employees.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "data".getBytes()), 99L));

        assertEquals("Only non-empty CSV files are supported", error.getMessage());
        verify(repository, never()).save(any());
    }

    private static EmployeeEntity employee(String identifier) {
        return new EmployeeEntity(profile(identifier));
    }

    private static EmployeeProfile profile(String identifier) {
        return new EmployeeProfile(identifier, "Test Employee", identifier.toLowerCase() + "@example.test",
                "ACTIVE", "FULL_TIME", LocalDate.of(2020, 1, 1), null, "US", "New York", "ACME Inc.",
                "Engineering", "Technology", "Engineer", "Engineering", "L4", null, "ENG-01", null);
    }
}
