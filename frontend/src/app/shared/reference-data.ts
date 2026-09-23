export interface ReferenceOption {
  code: string;
  name: string;
}

export const COUNTRIES: ReferenceOption[] = [
  { code: 'AE', name: 'United Arab Emirates' },
  { code: 'AU', name: 'Australia' },
  { code: 'BE', name: 'Belgium' },
  { code: 'BR', name: 'Brazil' },
  { code: 'CA', name: 'Canada' },
  { code: 'CH', name: 'Switzerland' },
  { code: 'CN', name: 'China' },
  { code: 'DE', name: 'Germany' },
  { code: 'DK', name: 'Denmark' },
  { code: 'ES', name: 'Spain' },
  { code: 'FI', name: 'Finland' },
  { code: 'FR', name: 'France' },
  { code: 'GB', name: 'United Kingdom' },
  { code: 'IE', name: 'Ireland' },
  { code: 'IN', name: 'India' },
  { code: 'IT', name: 'Italy' },
  { code: 'JP', name: 'Japan' },
  { code: 'LU', name: 'Luxembourg' },
  { code: 'MX', name: 'Mexico' },
  { code: 'NL', name: 'Netherlands' },
  { code: 'NO', name: 'Norway' },
  { code: 'NZ', name: 'New Zealand' },
  { code: 'PL', name: 'Poland' },
  { code: 'PT', name: 'Portugal' },
  { code: 'SE', name: 'Sweden' },
  { code: 'SG', name: 'Singapore' },
  { code: 'US', name: 'United States' },
  { code: 'ZA', name: 'South Africa' }
];

export const CURRENCIES: ReferenceOption[] = [
  { code: 'AED', name: 'UAE dirham' },
  { code: 'AUD', name: 'Australian dollar' },
  { code: 'BRL', name: 'Brazilian real' },
  { code: 'CAD', name: 'Canadian dollar' },
  { code: 'CHF', name: 'Swiss franc' },
  { code: 'CNY', name: 'Chinese yuan' },
  { code: 'DKK', name: 'Danish krone' },
  { code: 'EUR', name: 'Euro' },
  { code: 'GBP', name: 'Pound sterling' },
  { code: 'HKD', name: 'Hong Kong dollar' },
  { code: 'INR', name: 'Indian rupee' },
  { code: 'JPY', name: 'Japanese yen' },
  { code: 'MXN', name: 'Mexican peso' },
  { code: 'NOK', name: 'Norwegian krone' },
  { code: 'NZD', name: 'New Zealand dollar' },
  { code: 'PLN', name: 'Polish zloty' },
  { code: 'SEK', name: 'Swedish krona' },
  { code: 'SGD', name: 'Singapore dollar' },
  { code: 'USD', name: 'US dollar' },
  { code: 'ZAR', name: 'South African rand' }
];

export const REPORTING_CURRENCIES = CURRENCIES.filter(currency =>
  ['USD', 'GBP', 'CAD', 'INR', 'JPY'].includes(currency.code));
