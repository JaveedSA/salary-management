package com.acme.salarymanagement.imports;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.acme.salarymanagement.compensation.CompensationRepository;
import com.acme.salarymanagement.compensation.CompensationEntity;
import com.acme.salarymanagement.domain.CompensationType;
import com.acme.salarymanagement.domain.ApprovalStatus;
import com.acme.salarymanagement.domain.CompensationRecord;
import com.acme.salarymanagement.domain.EmployeeProfile;
import com.acme.salarymanagement.employee.EmployeeEntity;
import com.acme.salarymanagement.employee.EmployeeRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportService {

    private final ImportBatchRepository repository;
    private final EmployeeRepository employeeRepository;
    private final CompensationRepository compensationRepository;
    private final ImportFailureService failureService;

    public ImportService(ImportBatchRepository repository, EmployeeRepository employeeRepository,
            CompensationRepository compensationRepository, ImportFailureService failureService) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.compensationRepository = compensationRepository;
        this.failureService = failureService;
    }

    @Transactional
    @PreAuthorize("@authorizationService.canReviewImports(authentication)")
    public ImportBatchSummary stage(MultipartFile file, long uploaderId) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null
                || !file.getOriginalFilename().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new IllegalArgumentException("Only non-empty CSV files are supported");
        }
        try {
            List<String> lines = Arrays.stream(new String(file.getBytes(), StandardCharsets.UTF_8).split("\\R", -1))
                    .filter(line -> !line.isBlank())
                    .toList();
            if (lines.size() < 2) {
                throw new IllegalArgumentException("CSV must contain a header and at least one data row");
            }
            ImportBatchEntity batch = repository.save(
                    new ImportBatchEntity(file.getOriginalFilename(), uploaderId, lines.subList(1, lines.size())));
            return ImportBatchSummary.from(batch);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read CSV upload", exception);
        }
    }

    @Transactional
    @PreAuthorize("@authorizationService.canReviewImports(authentication)")
    public ImportBatchSummary validate(long batchId) {
        ImportBatchEntity batch = repository.findById(batchId).orElseThrow();
        Set<String> keys = new HashSet<>();
        int accepted = 0;
        int rejected = 0;
        for (ImportRowEntity row : batch.getRows()) {
            List<String> errors = validateRow(row.getRawData().split(",", -1), keys);
            if (errors.isEmpty()) {
                row.accept();
                accepted++;
            } else {
                row.reject(String.join("; ", errors));
                rejected++;
            }
        }
        batch.applyValidationCounts(accepted, rejected);
        return ImportBatchSummary.from(repository.save(batch));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canReviewImports(authentication)")
    public ImportBatchDetails details(long batchId) {
        return ImportBatchDetails.from(repository.findById(batchId).orElseThrow());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canReviewImports(authentication)")
    public String rejectedRowsCsv(long batchId) {
        ImportBatchEntity batch = repository.findById(batchId).orElseThrow();
        return batch.getRows().stream()
                .filter(row -> row.getValidationStatus() == ImportRowEntity.ValidationStatus.REJECTED)
                .map(row -> row.getRowNumber() + "," + row.getValidationErrors() + "," + row.getRawData())
                .collect(java.util.stream.Collectors.joining(System.lineSeparator(), "row_number,error,raw_data"
                        + System.lineSeparator(), ""));
    }

    @Transactional
    @PreAuthorize("@authorizationService.canApplyImports(authentication)")
    public ImportBatchSummary apply(long batchId, boolean confirmed) {
        if (!confirmed) {
            throw new IllegalArgumentException("Explicit confirmation is required");
        }
        ImportBatchEntity batch = repository.findById(batchId).orElseThrow();
        if (batch.getStatus() != com.acme.salarymanagement.domain.ImportStatus.VALIDATED) {
            throw new IllegalArgumentException("Only fully validated batches can be applied");
        }
        try {
            for (ImportRowEntity row : batch.getRows()) {
                applyRow(row.getRawData().split(",", -1));
            }
            batch.markApplied();
            return ImportBatchSummary.from(repository.save(batch));
        } catch (RuntimeException exception) {
            failureService.markFailed(batchId);
            throw exception;
        }
    }

    private void applyRow(String[] values) {
        if (values.length != 17) {
            throw new IllegalArgumentException("Validated row has an invalid column count");
        }
        EmployeeEntity employee = employeeRepository.findByEmployeeIdentifier(values[0]).orElseGet(() ->
                new EmployeeEntity(new EmployeeProfile(values[0], values[1], values[2], values[3], values[4],
                        java.time.LocalDate.parse(values[5]), null, values[6], values[7], null, values[8], null,
                        values[9], values[10], values[11], null, null, null)));
        employeeRepository.save(employee);
        CompensationType type = CompensationType.valueOf(values[12].toUpperCase(Locale.ROOT));
        compensationRepository.save(new CompensationEntity(new CompensationRecord(employee.getId(), type,
                Long.parseLong(values[13]), values[14].toUpperCase(Locale.ROOT), values[15],
                java.time.LocalDate.parse(values[16]), null, "Imported batch", ApprovalStatus.PENDING)));
    }

    private List<String> validateRow(String[] values, Set<String> keys) {
        List<String> errors = new java.util.ArrayList<>();
        if (values.length != 17) {
            errors.add("Expected 17 columns");
            return errors;
        }
        if (values[0].isBlank() || values[1].isBlank() || values[2].isBlank() || values[5].isBlank()
                || values[6].isBlank() || values[12].isBlank() || values[13].isBlank() || values[14].isBlank()
                || values[15].isBlank() || values[16].isBlank()) {
            errors.add("Required employee or compensation field is blank");
        }
        CompensationType type = null;
        try {
            type = CompensationType.valueOf(values[12].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            errors.add("Unsupported compensation type");
        }
        try {
            if (Long.parseLong(values[13]) < 0) errors.add("Amount must be non-negative");
        } catch (NumberFormatException exception) {
            errors.add("Amount must be an integer in minor units");
        }
        java.time.LocalDate effectiveFrom = null;
        try {
            effectiveFrom = java.time.LocalDate.parse(values[16]);
        } catch (java.time.format.DateTimeParseException exception) {
            errors.add("Effective date must use ISO format");
        }
        if (!values[14].matches("[A-Za-z]{3}")) errors.add("Currency must be an ISO 4217 code");
        String key = values[0] + "|" + values[12] + "|" + values[16];
        if (!keys.add(key)) errors.add("Duplicate employee, type, and effective date");
        if (type != null && effectiveFrom != null) {
            var employee = employeeRepository.findByEmployeeIdentifier(values[0]);
            if (employee.isPresent() && !compensationRepository
                    .findOverlapping(employee.get().getId(), type, effectiveFrom, null).isEmpty()) {
                errors.add("Compensation period overlaps an existing record");
            }
        }
        return errors;
    }
}
