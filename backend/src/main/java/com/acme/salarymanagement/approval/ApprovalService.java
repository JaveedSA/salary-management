package com.acme.salarymanagement.approval;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.acme.salarymanagement.audit.AuditEventService;
import com.acme.salarymanagement.compensation.CompensationEntity;
import com.acme.salarymanagement.compensation.CompensationRepository;
import com.acme.salarymanagement.domain.ApprovalStatus;
import com.acme.salarymanagement.employee.EmployeeEntity;
import com.acme.salarymanagement.employee.EmployeeRepository;
import com.acme.salarymanagement.imports.ImportBatchEntity;
import com.acme.salarymanagement.imports.ImportBatchRepository;
import com.acme.salarymanagement.security.AppUser;

@Service
public class ApprovalService {

    private final CompensationRepository compensationRepository;
    private final EmployeeRepository employeeRepository;
    private final ImportBatchRepository importBatchRepository;
    private final ApprovalEventRepository approvalRepository;
    private final AuditEventService auditEventService;

    @Autowired
    public ApprovalService(CompensationRepository compensationRepository, ImportBatchRepository importBatchRepository,
            ApprovalEventRepository approvalRepository, AuditEventService auditEventService,
            EmployeeRepository employeeRepository) {
        this.compensationRepository = compensationRepository;
        this.importBatchRepository = importBatchRepository;
        this.approvalRepository = approvalRepository;
        this.auditEventService = auditEventService;
        this.employeeRepository = employeeRepository;
    }

    public ApprovalService(CompensationRepository compensationRepository, ImportBatchRepository importBatchRepository,
            ApprovalEventRepository approvalRepository, AuditEventService auditEventService) {
        this(compensationRepository, importBatchRepository, approvalRepository, auditEventService, null);
    }

    @Transactional
    @PreAuthorize("@authorizationService.canDecideApprovals(authentication)")
    public ApprovalEventEntity decideCompensation(long compensationId, ApprovalStatus decision, String reason) {
        CompensationEntity compensation = compensationRepository.findById(compensationId).orElseThrow();
        if (decision == ApprovalStatus.REVERSED) {
            if (compensation.getStatus() != ApprovalStatus.APPROVED
                    && compensation.getStatus() != ApprovalStatus.REJECTED) {
                throw new IllegalArgumentException("Compensation record " + compensationId
                        + " cannot be reversed because it has no completed approval decision");
            }
        } else if (compensation.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Employee has no pending approval for compensation record "
                    + compensationId);
        }
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

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canDecideApprovals(authentication)")
    public List<PendingApprovalItem> pendingCompensation() {
        return compensationRepository.findByStatusOrderByEffectiveFromAsc(ApprovalStatus.PENDING).stream()
                .map(this::toPendingApprovalItem)
                .toList();
    }

    private PendingApprovalItem toPendingApprovalItem(CompensationEntity compensation) {
        EmployeeEntity employee = employeeRepository.findById(compensation.getEmployeeId()).orElseThrow();
        return new PendingApprovalItem(compensation.getId(), employee.getEmployeeIdentifier(),
                employee.toProfile().fullName(), compensation.toRecord());
    }

    private static Long actorId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof AppUser user ? user.getId() : null;
    }
}