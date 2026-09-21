package com.acme.salarymanagement.imports;

import java.util.List;

public record ImportBatchDetails(ImportBatchSummary batch, List<ImportRowSummary> rows) {

    public static ImportBatchDetails from(ImportBatchEntity batch) {
        return new ImportBatchDetails(ImportBatchSummary.from(batch),
                batch.getRows().stream().map(ImportRowSummary::from).toList());
    }
}
