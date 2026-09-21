package com.acme.salarymanagement.audit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.acme.salarymanagement.approval.ApprovalEventEntity;
import com.acme.salarymanagement.approval.ApprovalEventRepository;
import com.acme.salarymanagement.approval.ApprovalService;
import com.acme.salarymanagement.compensation.CompensationEntity;
import com.acme.salarymanagement.compensation.CompensationRepository;
import com.acme.salarymanagement.domain.ApprovalStatus;
import com.acme.salarymanagement.domain.CompensationRecord;
import com.acme.salarymanagement.domain.CompensationType;
import com.acme.salarymanagement.imports.ImportBatchRepository;
import com.acme.salarymanagement.security.AppUser;

class AuditAndApprovalServiceTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recordsImmutableAuditEventWithContext() throws Exception {
        AuditEventRepository repository = mock(AuditEventRepository.class);
        AuditEventService service = new AuditEventService(repository);
        when(repository.save(any(AuditEventEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        authenticatedUser();

        AuditEventEntity saved = service.record("COMPENSATION", 42L, "UPDATED", "old", "new",
                "annual review", "API", null, null);

        assertNotNull(saved.getCreatedAt());
        verify(repository).save(saved);
        assertThrows(NoSuchMethodException.class, () -> AuditEventService.class.getMethod("delete", long.class));
    }

    @Test
    void recordsApprovalAndReversalEvents() {
        CompensationRepository compensationRepository = mock(CompensationRepository.class);
        ImportBatchRepository importRepository = mock(ImportBatchRepository.class);
        ApprovalEventRepository approvalRepository = mock(ApprovalEventRepository.class);
        AuditEventService auditService = mock(AuditEventService.class);
        CompensationEntity compensation = new CompensationEntity(new CompensationRecord(7L,
                CompensationType.BASE_SALARY, 100_000, "USD", "MONTHLY", LocalDate.of(2026, 1, 1),
                null, "initial", ApprovalStatus.PENDING));
        when(compensationRepository.findById(12L)).thenReturn(Optional.of(compensation));
        when(approvalRepository.save(any(ApprovalEventEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        authenticatedUser();
        ApprovalService service = new ApprovalService(compensationRepository, importRepository, approvalRepository, auditService);

        service.decideCompensation(12L, ApprovalStatus.APPROVED, "reviewed");
        service.decideCompensation(12L, ApprovalStatus.REVERSED, "reversal");

        verify(approvalRepository, org.mockito.Mockito.times(2)).save(any(ApprovalEventEntity.class));
        ArgumentCaptor<String> action = ArgumentCaptor.forClass(String.class);
        verify(auditService, org.mockito.Mockito.times(2)).record(any(), any(Long.class), action.capture(), any(), any(), any(), any(), any(), any());
        org.junit.jupiter.api.Assertions.assertEquals(java.util.List.of("APPROVED", "REVERSED"), action.getAllValues());
    }

    private static void authenticatedUser() {
        Authentication authentication = mock(Authentication.class);
        AppUser user = mock(AppUser.class);
        when(user.getId()).thenReturn(99L);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
