import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApprovalEvent, ApprovalService, AuditEvent, PendingApprovalItem } from './approval.service';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './approval-workspace.component.html',
  styleUrl: './approval-workspace.component.scss'
})
export class ApprovalWorkspaceComponent implements OnInit {
  private readonly service = inject(ApprovalService);
  readonly form = inject(FormBuilder).nonNullable.group({
    compensationId: [0, [Validators.required, Validators.min(1)]],
    decision: ['APPROVED', Validators.required],
    reason: ['', Validators.required]
  });
  readonly auditForm = inject(FormBuilder).nonNullable.group({
    entityType: ['COMPENSATION', Validators.required], entityId: [0, [Validators.required, Validators.min(1)]]
  });
  readonly approvalSearch = new FormControl('', { nonNullable: true });
  event: ApprovalEvent | null = null;
  auditEvents: AuditEvent[] = [];
  pendingApprovals: PendingApprovalItem[] = [];
  loadingPending = false;
  queueMessage = '';
  decisionMessage = '';
  decisionMessageType: 'success' | 'error' | '' = '';
  auditMessage = '';

  get filteredPendingApprovals(): PendingApprovalItem[] {
    const query = this.approvalSearch.value.trim().toLowerCase();
    if (!query) return this.pendingApprovals;
    return this.pendingApprovals.filter(approval =>
      approval.employeeName.toLowerCase().includes(query)
      || approval.employeeIdentifier.toLowerCase().includes(query));
  }

  ngOnInit(): void {
    this.loadPendingApprovals();
  }

  loadPendingApprovals(): void {
    this.loadingPending = true;
    this.service.pendingCompensation().subscribe({
      next: approvals => { this.pendingApprovals = approvals; this.loadingPending = false; },
      error: () => { this.loadingPending = false; this.queueMessage = 'Pending approvals are unavailable for this account.'; }
    });
  }

  selectApproval(approval: PendingApprovalItem): void {
    this.form.reset({ compensationId: approval.compensationRecordId, decision: 'APPROVED', reason: '' });
    this.auditForm.patchValue({ entityType: 'COMPENSATION', entityId: approval.compensationRecordId });
    this.decisionMessage = `${approval.employeeName}'s ${this.formatType(approval.compensation.compensationType)} is selected for review.`;
    this.decisionMessageType = '';
  }

  formatType(type: string): string {
    return type.replace('_', ' ');
  }

  decide(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.decisionMessage = 'Complete the required fields before recording the decision.';
      this.decisionMessageType = 'error';
      return;
    }
    const value = this.form.getRawValue();
    this.service.decideCompensation(value.compensationId, value.decision, value.reason).subscribe({
      next: event => {
        this.event = event;
        this.form.reset({ compensationId: 0, decision: 'APPROVED', reason: '' });
        this.decisionMessage = 'Decision recorded and retained in approval history. Select another pending row to continue.';
        this.decisionMessageType = 'success';
        this.loadPendingApprovals();
      },
      error: response => {
        this.decisionMessage = response.error?.message
          || 'The decision could not be recorded. Check the selected record and try again.';
        this.decisionMessageType = 'error';
      }
    });
  }

  loadAudit(): void {
    if (this.auditForm.invalid) { this.auditForm.markAllAsTouched(); return; }
    const value = this.auditForm.getRawValue();
    this.service.auditHistory(value.entityType, value.entityId).subscribe({
      next: events => { this.auditEvents = events; this.auditMessage = 'Audit history loaded.'; },
      error: () => this.auditMessage = 'Audit history is unavailable for this account.'
    });
  }
}
