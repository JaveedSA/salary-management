package com.acme.salarymanagement.imports;

import java.time.LocalDateTime;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import com.acme.salarymanagement.domain.ImportStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "import_batch")
public class ImportBatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_filename", nullable = false)
    private String sourceFilename;

    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportStatus status;

    @Column(name = "total_rows", nullable = false)
    private int totalRows;

    @Column(name = "accepted_rows", nullable = false)
    private int acceptedRows;

    @Column(name = "rejected_rows", nullable = false)
    private int rejectedRows;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImportRowEntity> rows = new ArrayList<>();

    protected ImportBatchEntity() {
    }

    public ImportBatchEntity(String sourceFilename, Long uploadedBy, List<String> rawRows) {
        this.sourceFilename = sourceFilename;
        this.uploadedBy = uploadedBy;
        this.status = ImportStatus.STAGED;
        this.createdAt = LocalDateTime.now(Clock.systemUTC());
        this.totalRows = rawRows.size();
        this.acceptedRows = 0;
        this.rejectedRows = 0;
        for (int index = 0; index < rawRows.size(); index++) {
            rows.add(new ImportRowEntity(this, index + 1, rawRows.get(index)));
        }
    }

    public Long getId() {
        return id;
    }

    public String getSourceFilename() {
        return sourceFilename;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public List<ImportRowEntity> getRows() {
        return rows;
    }

    public void applyValidationCounts(int acceptedRows, int rejectedRows) {
        this.acceptedRows = acceptedRows;
        this.rejectedRows = rejectedRows;
        this.status = rejectedRows == 0 ? ImportStatus.VALIDATED : ImportStatus.STAGED;
    }

    public int getAcceptedRows() {
        return acceptedRows;
    }

    public int getRejectedRows() {
        return rejectedRows;
    }

    public void markApplied() {
        this.status = ImportStatus.APPLIED;
    }

    public void markPending() { this.status = ImportStatus.PENDING; }
    public void markApproved() { this.status = ImportStatus.APPROVED; }
    public void markRejected() { this.status = ImportStatus.REJECTED; }
    public void markReversed() { this.status = ImportStatus.REVERSED; }

    public void markFailed() {
        this.status = ImportStatus.FAILED;
    }
}
