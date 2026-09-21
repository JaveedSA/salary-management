export interface EmployeeProfile {
  employeeId?: number;
  employeeIdentifier: string;
  fullName: string;
  workEmail: string;
  employmentStatus: string;
  employmentType: string;
  hireDate: string;
  terminationDate?: string;
  country: string;
  location?: string;
  legalEntity?: string;
  department?: string;
  businessUnit?: string;
  jobTitle?: string;
  jobFamily?: string;
  jobLevel?: string;
  managerIdentifier?: string;
  costCenter?: string;
}

export type CompensationType = 'BASE_SALARY' | 'BONUS' | 'ALLOWANCE';
export type CompensationPeriod = 'HISTORICAL' | 'CURRENT' | 'FUTURE';

export interface CompensationTimelineItem {
  compensation: {
    compensationType: CompensationType;
    amountMinorUnits: number;
    currencyCode: string;
    payFrequency: string;
    effectiveFrom: string;
    effectiveUntil?: string;
    reason?: string;
    status: string;
  };
  period: CompensationPeriod;
}