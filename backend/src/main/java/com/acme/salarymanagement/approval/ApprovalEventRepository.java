package com.acme.salarymanagement.approval;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalEventRepository extends JpaRepository<ApprovalEventEntity, Long> {
    List<ApprovalEventEntity> findByCompensationRecordIdOrderByCreatedAtAsc(Long compensationRecordId);
    List<ApprovalEventEntity> findByImportBatchIdOrderByCreatedAtAsc(Long importBatchId);
}