import { expect, Page, test } from '@playwright/test';

async function mockApi(page: Page): Promise<void> {
  await page.route('**/api/**', async route => {
    const request = route.request();
    const url = new URL(request.url());

    if (url.pathname === '/api/auth/login') {
      await route.fulfill({ status: 200, body: '{}' });
      return;
    }
    if (url.pathname === '/api/auth/session') {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({
        username: 'manager', roles: ['HR_MANAGER']
      }) });
      return;
    }
    if (url.pathname === '/api/employees' && request.method() === 'GET') {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{
        employeeIdentifier: 'ACME-10001', fullName: 'Maya Chen', workEmail: 'maya.chen@acme.example',
        employmentStatus: 'ACTIVE', employmentType: 'FULL_TIME', hireDate: '2021-06-14', country: 'DE',
        location: 'Berlin', department: 'Engineering', jobTitle: 'Senior Software Engineer', employeeId: 1
      }]) });
      return;
    }
    if (url.pathname === '/api/compensation/employee/1') {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{
        compensation: { compensationType: 'BASE_SALARY', amountMinorUnits: 9200000, currencyCode: 'EUR',
          payFrequency: 'ANNUAL', effectiveFrom: '2026-01-01', status: 'APPROVED' }, period: 'CURRENT'
      }]) });
      return;
    }
    if (url.pathname === '/api/imports' && request.method() === 'POST') {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({
        id: 1, sourceFilename: 'sample.csv', status: 'PENDING', totalRows: 1, acceptedRows: 0, rejectedRows: 0
      }) });
      return;
    }
    if (url.pathname === '/api/imports/1/validate') {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({
        id: 1, sourceFilename: 'sample.csv', status: 'VALIDATED', totalRows: 1, acceptedRows: 1, rejectedRows: 0
      }) });
      return;
    }
    if (url.pathname === '/api/imports/1' && request.method() === 'GET') {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({
        batch: { id: 1, sourceFilename: 'sample.csv', status: 'VALIDATED', totalRows: 1, acceptedRows: 1, rejectedRows: 0 },
        rows: [{ rowNumber: 2, validationStatus: 'ACCEPTED', rawData: 'ACME-10001,Maya Chen', validationErrors: null }]
      }) });
      return;
    }
    if (url.pathname === '/api/imports/1/apply') {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({
        id: 1, sourceFilename: 'sample.csv', status: 'APPLIED', totalRows: 1, acceptedRows: 1, rejectedRows: 0
      }) });
      return;
    }
    if (url.pathname === '/api/reports/compensation/metrics') {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{
        currencyCode: 'EUR', employeeCount: 6, totalMinorUnits: 55200000, averageMinorUnits: 9200000,
        medianMinorUnits: 9200000, minimumMinorUnits: 9200000, maximumMinorUnits: 9200000,
        periodChangeMinorUnits: 0, selectedCompensationTypes: 'BASE_SALARY', dateBasis: 'null to null'
      }]) });
      return;
    }
    if (url.pathname === '/api/reports/compensation/metrics/aggregate') {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{
        currencyCode: 'EUR', employeeCount: 6, suppressed: false, totalMinorUnits: 55200000,
        averageMinorUnits: 9200000, medianMinorUnits: 9200000, minimumMinorUnits: 9200000,
        maximumMinorUnits: 9200000, periodChangeMinorUnits: 0, selectedCompensationTypes: 'BASE_SALARY',
        dateBasis: 'null to null'
      }]) });
      return;
    }
    if (url.pathname.startsWith('/api/approvals/compensation/')) {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({
        id: 1, decision: 'APPROVED', decisionReason: 'Reviewed', createdAt: '2026-09-21T00:00:00Z'
      }) });
      return;
    }
    if (url.pathname === '/api/audit/COMPENSATION/1') {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{
        action: 'APPROVED', source: 'API', reason: 'Reviewed', createdAt: '2026-09-21T00:00:00Z'
      }]) });
      return;
    }
    await route.fulfill({ status: 200, contentType: 'application/json', body: '{}' });
  });
}

async function signIn(page: Page): Promise<void> {
  await page.goto('/login');
  await page.getByLabel('Username').fill('manager');
  await page.getByLabel('Password').fill('secret');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await expect(page).toHaveURL(/\/dashboard$/);
}

test.describe('salary management browser workflows', () => {
  test.beforeEach(async ({ page }) => mockApi(page));

  test('logs in, enforces manager navigation, and searches employee compensation', async ({ page }) => {
    await signIn(page);
    await expect(page.getByText('Audit trail')).toBeVisible();

    await page.goto('/imports');
    await page.locator('#source-file').setInputFiles({ name: 'sample.csv', mimeType: 'text/csv', buffer: Buffer.from('employeeIdentifier,fullName\nACME-10001,Maya Chen\n') });
    await page.getByRole('button', { name: 'Stage upload' }).click();
    await expect(page.getByText('PENDING')).toBeVisible();
    await page.getByRole('button', { name: 'Validate rows' }).click();
    await expect(page.getByText('VALIDATED')).toBeVisible();
    await expect(page.getByText('ACCEPTED', { exact: true })).toBeVisible();
    await page.getByRole('button', { name: 'Confirm and apply' }).click();
    await expect(page.getByText('APPLIED', { exact: true })).toBeVisible();

    await page.goto('/employees');
    await page.getByLabel('Search by name or employee ID').fill('ACME-10001');
    await page.getByRole('button', { name: 'Search' }).click();
    await expect(page.getByText('Maya Chen')).toBeVisible();
    await page.getByRole('button', { name: /Maya Chen/ }).click();
    await expect(page.getByText('92,000.00 EUR')).toBeVisible();
  });

  test('runs reporting and records approval with audit history', async ({ page }) => {
    await signIn(page);

    await page.goto('/reports');
    await page.getByRole('button', { name: 'Calculate report' }).click();
    await expect(page.getByText('Metrics by currency')).toBeVisible();
    await expect(page.getByText('55,200,000 minor units')).toBeVisible();

    await page.goto('/approvals');
    await page.getByLabel('Compensation record ID').fill('1');
    await page.getByLabel('Reason').fill('Reviewed');
    await page.getByRole('button', { name: 'Record decision' }).click();
    await expect(page.getByText('Decision recorded and retained in approval history.')).toBeVisible();
    await page.getByLabel('Entity type').fill('COMPENSATION');
    await page.getByLabel('Entity ID').fill('1');
    await page.getByRole('button', { name: 'Load history' }).click();
    await expect(page.getByText('APPROVED')).toBeVisible();
  });
});
