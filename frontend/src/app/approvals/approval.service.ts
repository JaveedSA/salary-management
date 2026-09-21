import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

export interface ApprovalEvent {
  id: number;
  importBatchId: number | null;
  compensationRecordId: number | null;
  actorUserId: number;
  decision: string;
  decisionReason: string | null;
  createdAt: string;
}

export interface AuditEvent {
  id: number;
  actorUserId: number | null;
  entityType: string;
  entityId: number;
  action: string;
  previousValue: string | null;
  newValue: string | null;
  reason: string | null;
  source: string | null;
  importBatchId: number | null;
  approvalEventId: number | null;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class ApprovalService {
  private readonly http = inject(HttpClient);

  decideCompensation(id: number, decision: string, reason: string) {
    const params = new HttpParams().set('decision', decision).set('reason', reason);
    return this.http.post<ApprovalEvent>(`/api/approvals/compensation/${id}`, null, { params });
  }

  auditHistory(entityType: string, entityId: number) {
    return this.http.get<AuditEvent[]>(`/api/audit/${entityType}/${entityId}`);
  }
}
