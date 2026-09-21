package com.acme.salarymanagement.audit;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditHistoryService {

    private final AuditEventRepository repository;

    public AuditHistoryService(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@authorizationService.canReadAudit(authentication)")
    public List<AuditEventEntity> history(String entityType, long entityId) {
        return repository.findByEntityTypeAndEntityIdOrderByCreatedAtAsc(entityType, entityId);
    }
}