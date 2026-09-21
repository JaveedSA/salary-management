package com.acme.salarymanagement.audit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acme.salarymanagement.security.AppUser;

@Service
public class AuditEventService {

    private final AuditEventRepository repository;

    public AuditEventService(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AuditEventEntity record(String entityType, long entityId, String action,
            String previousValue, String newValue, String reason, String source,
            Long importBatchId, Long approvalEventId) {
        return repository.save(new AuditEventEntity(actorId(), entityType, entityId, action,
                previousValue, newValue, reason, source, importBatchId, approvalEventId));
    }

    private Long actorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUser user)) {
            return null;
        }
        return user.getId();
    }
}
