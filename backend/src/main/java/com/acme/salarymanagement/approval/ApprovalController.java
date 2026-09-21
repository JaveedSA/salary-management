package com.acme.salarymanagement.approval;

import java.util.List;

import com.acme.salarymanagement.domain.ApprovalStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService service;

    public ApprovalController(ApprovalService service) {
        this.service = service;
    }

    @PostMapping("/compensation/{compensationId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApprovalEventEntity decideCompensation(@PathVariable long compensationId,
            @RequestParam ApprovalStatus decision, @RequestParam(required = false) String reason) {
        return service.decideCompensation(compensationId, decision, reason);
    }

    @PostMapping("/imports/{batchId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApprovalEventEntity decideImport(@PathVariable long batchId,
            @RequestParam ApprovalStatus decision, @RequestParam(required = false) String reason) {
        return service.decideImport(batchId, decision, reason);
    }

    @GetMapping("/compensation/{compensationId}")
    public List<ApprovalEventEntity> compensationHistory(@PathVariable long compensationId) {
        return service.compensationHistory(compensationId);
    }
}
