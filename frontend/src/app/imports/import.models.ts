export interface ImportBatchSummary {
  id: number;
  sourceFilename: string;
  status: string;
  totalRows: number;
  acceptedRows: number;
  rejectedRows: number;
}

export interface ImportRowSummary {
  rowNumber: number;
  rawData: string;
  validationStatus: string;
  validationErrors: string | null;
}

export interface ImportBatchDetails {
  batch: ImportBatchSummary;
  rows: ImportRowSummary[];
}
