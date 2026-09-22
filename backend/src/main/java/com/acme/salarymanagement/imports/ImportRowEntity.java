package com.acme.salarymanagement.imports;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "import_row")
public class ImportRowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "import_batch_id", nullable = false, columnDefinition = "INTEGER")
    private ImportBatchEntity batch;

    @Column(name = "row_number", nullable = false)
    private int rowNumber;

    @Column(name = "raw_data", nullable = false)
    private String rawData;

    @Column(name = "validation_errors")
    private String validationErrors;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status", nullable = false)
    private ValidationStatus validationStatus;

    protected ImportRowEntity() {
    }

    public ImportRowEntity(ImportBatchEntity batch, int rowNumber, String rawData) {
        this.batch = batch;
        this.rowNumber = rowNumber;
        this.rawData = rawData;
        this.validationStatus = ValidationStatus.PENDING;
    }

    public enum ValidationStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public String getRawData() {
        return rawData;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public void accept() {
        validationStatus = ValidationStatus.ACCEPTED;
    }

    public void reject(String errors) {
        validationStatus = ValidationStatus.REJECTED;
        validationErrors = errors;
    }

    public String getValidationErrors() {
        return validationErrors;
    }
}
