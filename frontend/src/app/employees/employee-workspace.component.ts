import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EmployeeProfile, CompensationTimelineItem } from './employee.models';
import { EmployeeService } from './employee.service';
import { RouterLink } from '@angular/router';
import { COUNTRIES, CURRENCIES } from '../shared/reference-data';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './employee-workspace.component.html',
  styleUrl: './employee-workspace.component.scss'
})
export class EmployeeWorkspaceComponent {
  private readonly service = inject(EmployeeService);
  readonly countries = COUNTRIES;
  readonly currencies = CURRENCIES;
  readonly searchForm = inject(FormBuilder).nonNullable.group({ query: [''] });
  readonly profileForm = inject(FormBuilder).nonNullable.group({
    employeeIdentifier: ['', Validators.required], fullName: ['', Validators.required], workEmail: ['', [Validators.required, Validators.email]],
    employmentStatus: ['ACTIVE', Validators.required], employmentType: ['FULL_TIME', Validators.required], hireDate: ['', Validators.required],
    country: ['', Validators.required], location: [''], department: [''], jobTitle: [''], jobLevel: ['']
  });
  readonly compensationForm = inject(FormBuilder).nonNullable.group({
    compensationType: ['BASE_SALARY', Validators.required], amountMinorUnits: [0, [Validators.required, Validators.min(0)]],
    currencyCode: ['USD', [Validators.required, Validators.pattern(/^[A-Z]{3}$/)]], payFrequency: ['ANNUAL', Validators.required], effectiveFrom: ['', Validators.required], effectiveUntil: [''], reason: ['']
  });
  employees: EmployeeProfile[] = [];
  selected: EmployeeProfile | null = null;
  compensation: CompensationTimelineItem[] = [];
  loading = false;
  savingCompensation = false;
  message = '';

  search(): void {
    this.loading = true;
    this.service.search(this.searchForm.controls.query.value).subscribe({
      next: employees => { this.employees = employees; this.loading = false; },
      error: () => { this.message = 'Employee search is unavailable.'; this.loading = false; }
    });
  }

  select(employee: EmployeeProfile): void {
    this.selected = employee;
    this.compensation = [];
    if (employee.employeeId) this.service.compensation(employee.employeeId).subscribe({ next: items => this.compensation = items });
  }

  create(): void {
    if (this.profileForm.invalid) { this.profileForm.markAllAsTouched(); return; }
    this.service.create(this.profileForm.getRawValue()).subscribe({
      next: profile => { this.message = `${profile.fullName} added.`; this.profileForm.reset({ employmentStatus: 'ACTIVE', employmentType: 'FULL_TIME' }); this.search(); },
      error: () => this.message = 'Profile could not be created.'
    });
  }

  addCompensation(): void {
    const values = this.compensationForm.getRawValue();
    if (values.effectiveUntil && values.effectiveUntil < values.effectiveFrom) {
      this.message = 'The end date must be on or after the start date.';
      return;
    }
    if (!this.selected?.employeeId || this.compensationForm.invalid) { this.compensationForm.markAllAsTouched(); return; }
    this.savingCompensation = true;
    this.service.addCompensation({ employeeId: this.selected.employeeId, ...this.compensationForm.getRawValue() }).subscribe({
      next: () => {
        this.message = 'Compensation change saved.';
        this.savingCompensation = false;
        this.compensationForm.reset({ compensationType: 'BASE_SALARY', amountMinorUnits: 0,
          currencyCode: 'USD', payFrequency: 'ANNUAL', effectiveFrom: '', effectiveUntil: '', reason: '' });
        this.select(this.selected!);
      },
      error: () => { this.savingCompensation = false; this.message = 'Compensation change could not be added.'; }
    });
  }
}