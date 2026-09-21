package com.acme.salarymanagement.imports;

public record ImportRowSummary(int rowNumber, String rawData, String validationStatus, String validationErrors) {

    public static ImportRowSummary from(ImportRowEntity row) {
        return new ImportRowSummary(row.getRowNumber(), row.getRawData(), row.getValidationStatus().name(),
                row.getValidationErrors());
    }
}
