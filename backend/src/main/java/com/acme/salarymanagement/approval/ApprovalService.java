package com.acme.salarymanagement.approval;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acme.salarymanagement.audit.AuditEventService;
import com.acme.salarymanagement.compensation.CompensationEntity;
import com.acme.salarymanagement.compensation.CompensationRepository;
import com.acme.salarymanagement.domain.ApprovalStatus;
import com.acme.salarymanagement.imports.ImportBatchEntity;
import com.acme.salarymanagement.imports.ImportBatchRepository;
import com.acme.salarymanagement.security.AppUser;

@Service
public class ApprovalService {

    private final CompensationRepository compensationRepository;
    private final ImportBatchRepository importBatchRepository;
    private final ApprovalEventRepository approvalRepository;
    private final AuditEventService auditEventService;

    public ApprovalService(CompensationRepository compensationRepository, ImportBatchRepository importBatchRepository,
            ApprovalEventRepository approvalRepository, AuditEventService auditEventService) {
        this.compensationRepository = compensationRepository;
        this.importBatchRepository = importBatchRepository;
        this.approvalRepository = approvalRepository;
        this.auditEventService = auditEventService;
    }

    @Transactional
    @PreAuthorize("@authorizationService.canDecideApprovals(authentication)")
    public ApprovalEventEntity decideCompensation(long compensationId, ApprovalStatus decision, String reason) {
        CompensationEntity compensation = compensationRepository.findById(compensationId).orElseThrow();
        compensation.decide(decision, reason);
        compensationRepository.save(compensation);
        ApprovalEventEntity event = approvalRepository.save(new ApprovalEventEntity(null, compensationId,
                actorId(), decision, reason));
        auditEventService.record("COMPENSATION", compensationId, decision.name(), null, compensation.toRecord().toString(),
                reason, "APPROVAL", null, event.getId());
        return event;
    }

    @Transactional
    @PreAuthorize("@authorizationService.canDecideApprovals(authentication)")
    public ApprovalEventEntity decideImport(long batchId, ApprovalStatus decision, String reason) {
        ImportBatchEntity batch = importBatchRepository.findById(batchId).orElseThrow();
        if (decision == ApprovalStatus.APPROVED) batch.markApproved();
        else if (decision == ApprovalStatus.REJECTED) batch.markRejected();
        else if (decision == ApprovalStatus.REVERSED) batch.markReversed();
        else batch.markPending();
        importBatchRepository.save(batch);
        ApprovalEventEntity event = approvalRepository.save(new ApprovalEventEntity(batchId, null, actorId(), decision, reason));
        auditEventService.record("IMPORT_BATCH", batchId, decision.name(), null, batch.getStatus().name(),
                reason, "APPROVAL", batchId, event.getId());
        return event;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canDecideApprovals(authentication)")
    public List<ApprovalEventEntity> compensationHistory(long compensationId) {
        return approvalRepository.findByCompensationRecordIdOrderByCreatedAtAsc(compensationId);
    }

    private static Long actorId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof AppUser user ? user.getId() : null;
    }
}