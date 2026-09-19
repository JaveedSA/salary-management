## ADDED Requirements

### Requirement: Employee profile management
The system SHALL allow authorized users to create and maintain an employee profile containing a unique employee identifier, name, work email, employment status, employment type, hire date, termination date when applicable, country, location, legal entity, department, business unit, job title, job family, job level, manager, and cost center.

#### Scenario: Authorized user creates an employee profile
- **WHEN** an authorized user submits a profile with all required identity and organizational fields
- **THEN** the system SHALL create the employee with a unique identifier and make the profile available for compensation management and reporting

#### Scenario: Duplicate employee identifier is submitted
- **WHEN** a user submits a profile whose employee identifier already exists
- **THEN** the system SHALL reject the profile and identify the existing identifier conflict

### Requirement: Effective-dated compensation records
The system SHALL maintain separate effective-dated compensation records for each employee and compensation type, including amount in minor currency units, ISO currency code, pay frequency, effective start date, effective end date when known, reason, status, and audit references. Compensation periods for the same employee and type MUST NOT overlap.

#### Scenario: User records a future compensation change
- **WHEN** an authorized user submits a valid compensation record with an effective date after the current date
- **THEN** the system SHALL preserve the current record, store the future record, and identify it as future-dated

#### Scenario: Overlapping compensation period is submitted
- **WHEN** a user submits a compensation record whose effective period overlaps an existing record for the same employee and type
- **THEN** the system SHALL reject the change and report the conflicting period

### Requirement: Salary, bonus, and allowance types
The system SHALL support base salary, bonus, and allowance as distinct compensation types. Each type SHALL retain its own amount, currency, pay frequency, effective dates, reason, and approval status so reports can include or exclude types explicitly.

#### Scenario: Employee has multiple compensation types
- **WHEN** an employee has valid base salary, bonus, and allowance records
- **THEN** the system SHALL display and report each type separately and SHALL provide an explicit total only when the selected report requests a combined total

#### Scenario: Compensation type is omitted
- **WHEN** a compensation record is submitted without a supported compensation type
- **THEN** the system SHALL reject the record and list the supported types

### Requirement: Compensation detail and history
The system SHALL allow an authorized user to view an employee's current, historical, and approved future compensation records, including native amounts, currency, effective dates, status, change reason, and approval information.

#### Scenario: User views an employee history
- **WHEN** an authorized user requests an employee's compensation history
- **THEN** the system SHALL return records ordered by effective date and SHALL distinguish current, historical, and future records

#### Scenario: Employee has no compensation for a selected type
- **WHEN** a user requests a compensation type with no records for the employee
- **THEN** the system SHALL return an empty result for that type without substituting another compensation type