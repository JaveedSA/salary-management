import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ImportBatchDetails, ImportBatchSummary } from './import.models';

@Injectable({ providedIn: 'root' })
export class ImportService {
  private readonly http = inject(HttpClient);

  stage(file: File) {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<ImportBatchSummary>('/api/imports', form);
  }

  validate(batchId: number) {
    return this.http.post<ImportBatchSummary>(`/api/imports/${batchId}/validate`, {});
  }

  details(batchId: number) {
    return this.http.get<ImportBatchDetails>(`/api/imports/${batchId}`);
  }

  rejectedRows(batchId: number) {
    return this.http.get(`/api/imports/${batchId}/rejected`, { responseType: 'blob' });
  }

  apply(batchId: number) {
    return this.http.post<ImportBatchSummary>(`/api/imports/${batchId}/apply`, {}, {
      params: { confirm: true }
    });
  }
}
