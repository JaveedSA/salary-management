import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApprovalEvent, ApprovalService, AuditEvent } from './approval.service';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './approval-workspace.component.html',
  styleUrl: './approval-workspace.component.scss'
})
export class ApprovalWorkspaceComponent {
  private readonly service = inject(ApprovalService);
  readonly form = inject(FormBuilder).nonNullable.group({
    compensationId: [0, [Validators.required, Validators.min(1)]],
    decision: ['APPROVED', Validators.required],
    reason: ['', Validators.required]
  });
  readonly auditForm = inject(FormBuilder).nonNullable.group({
    entityType: ['COMPENSATION', Validators.required], entityId: [0, [Validators.required, Validators.min(1)]]
  });
  event: ApprovalEvent | null = null;
  auditEvents: AuditEvent[] = [];
  message = '';

  decide(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const value = this.form.getRawValue();
    this.service.decideCompensation(value.compensationId, value.decision, value.reason).subscribe({
      next: event => { this.event = event; this.message = 'Decision recorded and retained in approval history.'; },
      error: () => this.message = 'The decision could not be recorded.'
    });
  }

  loadAudit(): void {
    if (this.auditForm.invalid) { this.auditForm.markAllAsTouched(); return; }
    const value = this.auditForm.getRawValue();
    this.service.auditHistory(value.entityType, value.entityId).subscribe({
      next: events => { this.auditEvents = events; this.message = 'Audit history loaded.'; },
      error: () => this.message = 'Audit history is unavailable for this account.'
    });
  }
}
