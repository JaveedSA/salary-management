package com.acme.salarymanagement.compensation;

import java.time.LocalDate;

import com.acme.salarymanagement.domain.ApprovalStatus;
import com.acme.salarymanagement.domain.CompensationRecord;
import com.acme.salarymanagement.domain.CompensationType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "compensation_record")
public class CompensationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    private Long id;

    @Column(name = "employee_id", nullable = false, columnDefinition = "INTEGER")
    private long employeeId;
    @Enumerated(EnumType.STRING)
    @Column(name = "compensation_type", nullable = false)
    private CompensationType compensationType;
    @Column(name = "amount_minor_units", nullable = false, columnDefinition = "INTEGER")
    private long amountMinorUnits;
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;
    @Column(name = "pay_frequency", nullable = false)
    private String payFrequency;
    @Column(name = "effective_from", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = com.acme.salarymanagement.persistence.IsoLocalDateConverter.class)
    private LocalDate effectiveFrom;
    @Column(name = "effective_until", columnDefinition = "TEXT")
    @Convert(converter = com.acme.salarymanagement.persistence.IsoLocalDateConverter.class)
    private LocalDate effectiveUntil;
    private String reason;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status;

    protected CompensationEntity() {
    }

    public CompensationEntity(CompensationRecord compensationRecord) {
        employeeId = compensationRecord.employeeId();
        compensationType = compensationRecord.compensationType();
        amountMinorUnits = compensationRecord.amountMinorUnits();
        currencyCode = compensationRecord.currencyCode();
        payFrequency = compensationRecord.payFrequency();
        effectiveFrom = compensationRecord.effectiveFrom();
        effectiveUntil = compensationRecord.effectiveUntil();
        reason = compensationRecord.reason();
        status = compensationRecord.status();
    }

    public CompensationRecord toRecord() {
        return new CompensationRecord(employeeId, compensationType, amountMinorUnits, currencyCode, payFrequency,
                effectiveFrom, effectiveUntil, reason, status);
    }

    public long getEmployeeId() { return employeeId; }
    public Long getId() { return id; }
    public ApprovalStatus getStatus() { return status; }
    public void decide(ApprovalStatus decision, String reason) {
        status = decision;
        this.reason = reason == null ? this.reason : reason;
    }
    public CompensationType getCompensationType() { return compensationType; }
    public long getAmountMinorUnits() { return amountMinorUnits; }
    public String getCurrencyCode() { return currencyCode; }
    public String getPayFrequency() { return payFrequency; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public LocalDate getEffectiveUntil() { return effectiveUntil; }
}