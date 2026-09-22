package com.acme.salarymanagement.approval;

import java.time.Instant;

import com.acme.salarymanagement.domain.ApprovalStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "approval_event")
public class ApprovalEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    private Long id;
    @Column(name = "import_batch_id", columnDefinition = "INTEGER")
    private Long importBatchId;
    @Column(name = "compensation_record_id", columnDefinition = "INTEGER")
    private Long compensationRecordId;
    @Column(name = "actor_user_id", nullable = false, columnDefinition = "INTEGER")
    private Long actorUserId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus decision;
    @Column(name = "decision_reason")
    private String decisionReason;
    @Column(name = "created_at", nullable = false, columnDefinition = "TEXT")
    private Instant createdAt;

    protected ApprovalEventEntity() { }

    public ApprovalEventEntity(Long importBatchId, Long compensationRecordId, Long actorUserId,
            ApprovalStatus decision, String decisionReason) {
        this.importBatchId = importBatchId;
        this.compensationRecordId = compensationRecordId;
        this.actorUserId = actorUserId;
        this.decision = decision;
        this.decisionReason = decisionReason;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getImportBatchId() { return importBatchId; }
    public Long getCompensationRecordId() { return compensationRecordId; }
    public Long getActorUserId() { return actorUserId; }
    public ApprovalStatus getDecision() { return decision; }
    public String getDecisionReason() { return decisionReason; }
    public Instant getCreatedAt() { return createdAt; }
}