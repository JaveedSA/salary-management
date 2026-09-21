import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { CompensationAggregateMetric, CompensationMetric, CompensationReportEntry } from './report.models';

export interface ReportFilters {
  country?: string;
  department?: string;
  compensationType: string;
  effectiveFrom?: string;
  effectiveUntil?: string;
  reportingCurrency?: string;
}

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly http = inject(HttpClient);

  private params(filters: ReportFilters): HttpParams {
    let params = new HttpParams();
    Object.entries(filters).forEach(([key, value]) => { if (value) params = params.set(key, value); });
    return params;
  }

  entries(filters: ReportFilters) {
    return this.http.get<CompensationReportEntry[]>('/api/reports/compensation', { params: this.params(filters) });
  }

  metrics(filters: ReportFilters) {
    return this.http.get<CompensationMetric[]>('/api/reports/compensation/metrics', { params: this.params(filters) });
  }

  aggregates(filters: ReportFilters) {
    return this.http.get<CompensationAggregateMetric[]>('/api/reports/compensation/metrics/aggregate', { params: this.params(filters) });
  }
}
