package com.acme.salarymanagement.approval;

import com.acme.salarymanagement.domain.CompensationRecord;

public record PendingApprovalItem(
        long compensationRecordId,
        String employeeIdentifier,
        String employeeName,
        CompensationRecord compensation) {
}