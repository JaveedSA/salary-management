import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';

@Component({ standalone: true, imports: [CommonModule], templateUrl: './dashboard.component.html', styleUrl: './dashboard.component.scss' })
export class DashboardComponent {
  readonly auth = inject(AuthService);
  readonly priorities = [
    { label: 'Employees', detail: 'Profiles and organizational data', value: '10,000+' },
    { label: 'Compensation changes', detail: 'Awaiting review', value: '24' },
    { label: 'Countries', detail: 'Across the organization', value: '18' }
  ];
}