package com.acme.salarymanagement.audit;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_event")
public class AuditEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    private Long id;
    @Column(name = "actor_user_id", columnDefinition = "INTEGER")
    private Long actorUserId;
    @Column(name = "entity_type", nullable = false)
    private String entityType;
    @Column(name = "entity_id", nullable = false, columnDefinition = "INTEGER")
    private long entityId;
    @Column(nullable = false)
    private String action;
    @Column(name = "previous_value")
    private String previousValue;
    @Column(name = "new_value")
    private String newValue;
    private String reason;
    private String source;
    @Column(name = "import_batch_id", columnDefinition = "INTEGER")
    private Long importBatchId;
    @Column(name = "approval_event_id", columnDefinition = "INTEGER")
    private Long approvalEventId;
    @Column(name = "created_at", nullable = false, columnDefinition = "TEXT")
    private Instant createdAt;

    protected AuditEventEntity() {
    }

    public AuditEventEntity(Long actorUserId, String entityType, long entityId, String action,
            String previousValue, String newValue, String reason, String source,
            Long importBatchId, Long approvalEventId) {
        this.actorUserId = actorUserId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.previousValue = previousValue;
        this.newValue = newValue;
        this.reason = reason;
        this.source = source;
        this.importBatchId = importBatchId;
        this.approvalEventId = approvalEventId;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getActorUserId() { return actorUserId; }
    public String getEntityType() { return entityType; }
    public long getEntityId() { return entityId; }
    public String getAction() { return action; }
    public String getPreviousValue() { return previousValue; }
    public String getNewValue() { return newValue; }
    public String getReason() { return reason; }
    public String getSource() { return source; }
    public Long getImportBatchId() { return importBatchId; }
    public Long getApprovalEventId() { return approvalEventId; }
    public Instant getCreatedAt() { return createdAt; }
}
