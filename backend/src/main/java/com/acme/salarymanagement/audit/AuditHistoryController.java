package com.acme.salarymanagement.audit;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditHistoryController {

    private final AuditHistoryService service;

    public AuditHistoryController(AuditHistoryService service) {
        this.service = service;
    }

    @GetMapping("/{entityType}/{entityId}")
    public List<AuditEventEntity> history(@PathVariable String entityType, @PathVariable long entityId) {
        return service.history(entityType, entityId);
    }
}