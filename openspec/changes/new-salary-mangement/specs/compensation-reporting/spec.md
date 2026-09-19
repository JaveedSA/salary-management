## ADDED Requirements

### Requirement: Filterable compensation reporting
The system SHALL allow authorized users to filter compensation reports by country, location, legal entity, department, business unit, job family, job level, employment status, employment type, currency, compensation type, and effective date.

#### Scenario: User runs a filtered report
- **WHEN** an authorized user selects valid dimensions and a date basis
- **THEN** the system SHALL return results limited to employees and compensation records matching those filters

#### Scenario: User requests an invalid date range
- **WHEN** a report request has an end date before its start date
- **THEN** the system SHALL reject the request and identify the invalid date range

### Requirement: Compensation metrics and transparency
The system SHALL provide employee count, total, average, median, minimum, maximum, and change metrics for selected compensation types. Each result SHALL show the population count, date basis, native or reporting currency, and applied filters.

#### Scenario: User views average base salary by country
- **WHEN** an authorized user selects base salary, country, and a valid effective date
- **THEN** the system SHALL return the average for each country with the employee count and calculation context

#### Scenario: User requests a combined compensation metric
- **WHEN** an authorized user selects base salary, bonus, and allowance for a combined metric
- **THEN** the system SHALL include only the selected types and clearly label the combined result

### Requirement: Currency normalization
The system SHALL preserve native compensation amounts and SHALL identify the FX rate source, FX rate date, and reporting currency whenever a report converts amounts across currencies.

#### Scenario: Report uses currency conversion
- **WHEN** a report includes records in multiple currencies and requests a reporting currency
- **THEN** the system SHALL return normalized values together with the conversion assumptions and SHALL leave native values available

#### Scenario: FX information is unavailable
- **WHEN** a requested conversion lacks a valid rate for the selected date
- **THEN** the system SHALL identify the unavailable conversion and SHALL not silently substitute an undisclosed rate

### Requirement: Privacy-aware aggregate reporting
The system SHALL suppress or restrict aggregate results whose population is below the configured privacy threshold and SHALL not expose individual salary values through an aggregate-only report.

#### Scenario: Report population is below threshold
- **WHEN** a filtered report matches fewer employees than the configured minimum group size
- **THEN** the system SHALL suppress the affected aggregate and explain that the result is restricted for privacy

#### Scenario: Report has sufficient population
- **WHEN** a filtered report meets the configured minimum group size
- **THEN** the system SHALL return the permitted aggregate metrics and employee count