import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({ standalone: true, imports: [CommonModule, RouterLink], templateUrl: './dashboard.component.html', styleUrl: './dashboard.component.scss' })
export class DashboardComponent {
  readonly auth = inject(AuthService);
  readonly currentDate = new Date();
  readonly reportingQuarter = `Q${Math.floor(this.currentDate.getMonth() / 3) + 1}`;
  readonly priorities = [
    { label: 'Employees', detail: 'Profiles and organizational data', value: '10,000+' },
    { label: 'Compensation changes', detail: 'Awaiting review', value: '24' },
    { label: 'Countries', detail: 'Across the organization', value: '18' }
  ];
}