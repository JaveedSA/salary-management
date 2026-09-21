import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { EmployeeProfile, CompensationTimelineItem } from './employee.models';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private readonly http = inject(HttpClient);

  search(query: string) {
    return this.http.get<EmployeeProfile[]>('/api/employees', {
      params: new HttpParams().set('query', query)
    });
  }

  compensation(employeeId: number) {
    return this.http.get<CompensationTimelineItem[]>(`/api/compensation/employee/${employeeId}`);
  }

  addCompensation(payload: object) {
    return this.http.post('/api/compensation', payload);
  }

  create(profile: EmployeeProfile) {
    return this.http.post<EmployeeProfile>('/api/employees', profile);
  }
}