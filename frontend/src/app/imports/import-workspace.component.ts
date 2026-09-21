import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ImportBatchDetails, ImportBatchSummary } from './import.models';
import { ImportService } from './import.service';

@Component({
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import-workspace.component.html',
  styleUrl: './import-workspace.component.scss'
})
export class ImportWorkspaceComponent {
  private readonly service = inject(ImportService);
  file: File | null = null;
  batch: ImportBatchSummary | null = null;
  details: ImportBatchDetails | null = null;
  busy = false;
  message = '';

  choose(event: Event): void {
    this.file = (event.target as HTMLInputElement).files?.[0] ?? null;
    this.message = this.file ? `${this.file.name} is ready to stage.` : '';
  }

  upload(): void {
    if (!this.file) { this.message = 'Choose a CSV file first.'; return; }
    this.busy = true;
    this.service.stage(this.file).subscribe({
      next: batch => { this.batch = batch; this.details = null; this.message = 'Upload staged. Validate it before applying changes.'; this.busy = false; },
      error: () => { this.message = 'The upload could not be staged.'; this.busy = false; }
    });
  }

  validate(): void {
    if (!this.batch) return;
    this.busy = true;
    this.service.validate(this.batch.id).subscribe({
      next: batch => { this.batch = batch; this.loadDetails(); },
      error: () => { this.message = 'Validation failed. The staged batch was not applied.'; this.busy = false; }
    });
  }

  loadDetails(): void {
    if (!this.batch) return;
    this.service.details(this.batch.id).subscribe({
      next: details => { this.details = details; this.message = details.batch.status === 'VALIDATED' ? 'All rows are accepted. Confirm to apply.' : 'Review rejected rows and correct the source file.'; this.busy = false; },
      error: () => { this.message = 'Batch preview could not be loaded.'; this.busy = false; }
    });
  }

  downloadRejected(): void {
    if (!this.batch) return;
    this.service.rejectedRows(this.batch.id).subscribe(blob => {
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `rejected-${this.batch!.id}.csv`;
      link.click();
      URL.revokeObjectURL(url);
    });
  }

  apply(): void {
    if (!this.batch || this.batch.status !== 'VALIDATED') return;
    this.busy = true;
    this.service.apply(this.batch.id).subscribe({
      next: batch => { this.batch = batch; this.message = 'Batch applied successfully.'; this.loadDetails(); },
      error: () => { this.message = 'Application failed. No partial authoritative changes were kept.'; this.busy = false; this.loadDetails(); }
    });
  }
}
