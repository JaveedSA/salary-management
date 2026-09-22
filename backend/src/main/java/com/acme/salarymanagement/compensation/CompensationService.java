package com.acme.salarymanagement.compensation;

import java.util.List;
import java.time.LocalDate;
import java.util.Locale;

import com.acme.salarymanagement.domain.ApprovalStatus;
import com.acme.salarymanagement.domain.CompensationPeriod;
import com.acme.salarymanagement.domain.CompensationRecord;
import com.acme.salarymanagement.domain.CompensationTimelineItem;
import com.acme.salarymanagement.audit.AuditEventService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompensationService {

    private final CompensationRepository repository;
    private final AuditEventService auditEventService;

    public CompensationService(CompensationRepository repository, AuditEventService auditEventService) {
        this.repository = repository;
        this.auditEventService = auditEventService;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canReadCompensation(authentication, #employeeId)")
    public List<CompensationTimelineItem> history(long employeeId) {
        return repository.findByEmployeeIdOrderByEffectiveFromDesc(employeeId)
                .stream().map(entity -> new CompensationTimelineItem(entity.getId(), entity.toRecord(),
                    classify(entity.toRecord(), LocalDate.now())))
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
        String currencyCode = compensationRecord.currencyCode().toUpperCase(Locale.ROOT);
        List<CompensationEntity> overlapping = repository.findOverlapping(compensationRecord.employeeId(),
            compensationRecord.compensationType(), compensationRecord.effectiveFrom(),
            compensationRecord.effectiveUntil());
        CompensationEntity identical = overlapping.stream()
            .filter(existing -> isIdentical(existing.toRecord(), compensationRecord, currencyCode))
            .findFirst().orElse(null);
        if (identical != null) {
            return identical.toRecord();
        }
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Compensation period overlaps an existing record");
        }
        ApprovalStatus status = compensationRecord.status() == null ? ApprovalStatus.PENDING : compensationRecord.status();
        CompensationRecord normalizedRecord = new CompensationRecord(compensationRecord.employeeId(),
            compensationRecord.compensationType(), compensationRecord.amountMinorUnits(),
            currencyCode, compensationRecord.payFrequency(),
            compensationRecord.effectiveFrom(), compensationRecord.effectiveUntil(), compensationRecord.reason(), status);
        CompensationEntity saved = repository.save(new CompensationEntity(normalizedRecord));
        auditEventService.record("COMPENSATION", saved.getId(), "CREATED", null, normalizedRecord.toString(),
            normalizedRecord.reason(), "API", null, null);
        return saved.toRecord();
    }

    private static boolean isIdentical(CompensationRecord existing, CompensationRecord requested, String currencyCode) {
        return existing.employeeId() == requested.employeeId()
                && existing.compensationType() == requested.compensationType()
                && existing.amountMinorUnits() == requested.amountMinorUnits()
                && existing.currencyCode().equals(currencyCode)
                && existing.payFrequency().equals(requested.payFrequency())
                && existing.effectiveFrom().equals(requested.effectiveFrom())
                && java.util.Objects.equals(existing.effectiveUntil(), requested.effectiveUntil())
                && java.util.Objects.equals(existing.reason(), requested.reason());
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