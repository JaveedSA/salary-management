import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CompensationAggregateMetric, CompensationMetric } from './report.models';
import { ReportFilters, ReportService } from './report.service';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './report-workspace.component.html',
  styleUrl: './report-workspace.component.scss'
})
export class ReportWorkspaceComponent {
  private readonly service = inject(ReportService);
  readonly form = inject(FormBuilder).nonNullable.group({
    country: [''], department: [''], compensationType: ['BASE_SALARY', Validators.required],
    effectiveFrom: [''], effectiveUntil: [''], reportingCurrency: ['']
  });
  metrics: CompensationMetric[] = [];
  aggregates: CompensationAggregateMetric[] = [];
  busy = false;
  message = '';

  run(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const filters = this.form.getRawValue() as ReportFilters;
    if (filters.effectiveFrom && filters.effectiveUntil && filters.effectiveUntil < filters.effectiveFrom) {
      this.message = 'The report end date must not precede the start date.';
      return;
    }
    this.busy = true;
    this.service.metrics(filters).subscribe({
      next: metrics => {
        this.metrics = metrics;
        this.service.aggregates(filters).subscribe({
          next: aggregates => { this.aggregates = aggregates; this.message = 'Report calculated with the selected filters.'; this.busy = false; },
          error: () => { this.message = 'Privacy summary could not be loaded.'; this.busy = false; }
        });
      },
      error: () => { this.message = 'Report could not be calculated.'; this.busy = false; }
    });
  }
}
