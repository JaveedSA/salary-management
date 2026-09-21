export interface CompensationReportEntry {
  employeeIdentifier: string;
  country: string;
  location: string;
  department: string;
  compensationType: string;
  amountMinorUnits: number;
  currencyCode: string;
  payFrequency: string;
  effectiveFrom: string;
  effectiveUntil: string | null;
}

export interface CompensationMetric {
  currencyCode: string;
  employeeCount: number;
  totalMinorUnits: number;
  averageMinorUnits: number;
  medianMinorUnits: number;
  minimumMinorUnits: number;
  maximumMinorUnits: number;
  periodChangeMinorUnits: number;
  selectedCompensationTypes: string;
  dateBasis: string;
}

export interface CompensationAggregateMetric {
  currencyCode: string;
  employeeCount: number;
  suppressed: boolean;
  suppressionReason: string | null;
  totalMinorUnits: number | null;
  averageMinorUnits: number | null;
  medianMinorUnits: number | null;
  minimumMinorUnits: number | null;
  maximumMinorUnits: number | null;
  periodChangeMinorUnits: number;
  selectedCompensationTypes: string;
  dateBasis: string;
}
