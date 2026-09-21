package com.acme.salarymanagement.compensation;

import java.util.List;
import java.time.LocalDate;
import java.util.Locale;

import com.acme.salarymanagement.domain.ApprovalStatus;
import com.acme.salarymanagement.domain.CompensationPeriod;
import com.acme.salarymanagement.domain.CompensationRecord;
import com.acme.salarymanagement.domain.CompensationTimelineItem;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompensationService {

    private final CompensationRepository repository;

    public CompensationService(CompensationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canReadCompensation(authentication, #employeeId)")
    public List<CompensationTimelineItem> history(long employeeId) {
        return repository.findByEmployeeIdOrderByEffectiveFromDesc(employeeId)
                .stream().map(CompensationEntity::toRecord)
                .map(compensationRecord -> new CompensationTimelineItem(compensationRecord,
                    classify(compensationRecord, LocalDate.now())))
                .toList();
    }

    @Transactional
    @PreAuthorize("@authorizationService.canManageCompensation(authentication)")
    public CompensationRecord create(CompensationRecord compensationRecord) {
        if (compensationRecord == null || compensationRecord.amountMinorUnits() < 0
            || compensationRecord.compensationType() == null || compensationRecord.effectiveFrom() == null
            || compensationRecord.effectiveUntil() != null
                && compensationRecord.effectiveUntil().isBefore(compensationRecord.effectiveFrom())
            || compensationRecord.currencyCode() == null
            || !compensationRecord.currencyCode().matches("[A-Z]{3}")
            || compensationRecord.payFrequency() == null || compensationRecord.payFrequency().isBlank()) {
            throw new IllegalArgumentException("Invalid compensation record: check type, amount, currency, frequency, and dates");
        }
        if (!repository.findOverlapping(compensationRecord.employeeId(), compensationRecord.compensationType(),
                compensationRecord.effectiveFrom(), compensationRecord.effectiveUntil()).isEmpty()) {
            throw new IllegalArgumentException("Compensation period overlaps an existing record");
        }
        ApprovalStatus status = compensationRecord.status() == null ? ApprovalStatus.PENDING : compensationRecord.status();
        CompensationRecord normalizedRecord = new CompensationRecord(compensationRecord.employeeId(),
            compensationRecord.compensationType(), compensationRecord.amountMinorUnits(),
            compensationRecord.currencyCode().toUpperCase(Locale.ROOT), compensationRecord.payFrequency(),
            compensationRecord.effectiveFrom(), compensationRecord.effectiveUntil(), compensationRecord.reason(), status);
        return repository.save(new CompensationEntity(normalizedRecord)).toRecord();
    }

    private static CompensationPeriod classify(CompensationRecord compensationRecord, LocalDate today) {
        if (compensationRecord.effectiveFrom().isAfter(today)) {
            return CompensationPeriod.FUTURE;
        }
        if (compensationRecord.effectiveUntil() != null && compensationRecord.effectiveUntil().isBefore(today)) {
            return CompensationPeriod.HISTORICAL;
        }
        return CompensationPeriod.CURRENT;
    }
}