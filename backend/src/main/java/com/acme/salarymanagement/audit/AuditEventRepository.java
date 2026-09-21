package com.acme.salarymanagement.audit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {

    List<AuditEventEntity> findByEntityTypeAndEntityIdOrderByCreatedAtAsc(String entityType, long entityId);
}
