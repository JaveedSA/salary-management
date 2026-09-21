import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ReportWorkspaceComponent } from './report-workspace.component';

describe('ReportWorkspaceComponent', () => {
  let http: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportWorkspaceComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('rejects an invalid date range without requesting a report', () => {
    const fixture = TestBed.createComponent(ReportWorkspaceComponent);
    const component = fixture.componentInstance;
    component.form.patchValue({ effectiveFrom: '2026-02-01', effectiveUntil: '2026-01-01' });

    component.run();

    expect(component.message).toContain('end date must not precede');
  });

  it('requests native metrics and privacy aggregates using selected filters', () => {
    const fixture = TestBed.createComponent(ReportWorkspaceComponent);
    const component = fixture.componentInstance;
    component.form.patchValue({ country: 'GB', compensationType: 'BASE_SALARY,BONUS', reportingCurrency: 'USD' });

    component.run();

    const metricsRequest = http.expectOne(request => request.url === '/api/reports/compensation/metrics');
    expect(metricsRequest.request.params.get('country')).toBe('GB');
    expect(metricsRequest.request.params.get('compensationType')).toBe('BASE_SALARY,BONUS');
    expect(metricsRequest.request.params.get('reportingCurrency')).toBe('USD');
    metricsRequest.flush([{
      currencyCode: 'GBP', employeeCount: 6, totalMinorUnits: 600000,
      averageMinorUnits: 100000, medianMinorUnits: 100000, minimumMinorUnits: 80000,
      maximumMinorUnits: 120000, periodChangeMinorUnits: 0,
      selectedCompensationTypes: 'BASE_SALARY,BONUS', dateBasis: 'null to null'
    }]);

    const aggregateRequest = http.expectOne(request => request.url === '/api/reports/compensation/metrics/aggregate');
    aggregateRequest.flush([{
      currencyCode: 'GBP', employeeCount: 3, suppressed: true,
      suppressionReason: 'Aggregate restricted because the employee population is below 5',
      totalMinorUnits: null, averageMinorUnits: null, medianMinorUnits: null,
      minimumMinorUnits: null, maximumMinorUnits: null, periodChangeMinorUnits: 0,
      selectedCompensationTypes: 'BASE_SALARY,BONUS', dateBasis: 'null to null'
    }]);

    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('below 5');
    expect(fixture.nativeElement.textContent).not.toContain('600000');
  });
});
