package com.acme.salarymanagement.imports;

import com.acme.salarymanagement.domain.ImportStatus;

public record ImportBatchSummary(
        long id,
        String sourceFilename,
        ImportStatus status,
        int totalRows,
        int acceptedRows,
        int rejectedRows) {

    public static ImportBatchSummary from(ImportBatchEntity batch) {
        return new ImportBatchSummary(batch.getId(), batch.getSourceFilename(), batch.getStatus(),
            batch.getTotalRows(), batch.getAcceptedRows(), batch.getRejectedRows());
    }
}
