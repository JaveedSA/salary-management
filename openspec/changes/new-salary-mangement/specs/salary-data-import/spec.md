## ADDED Requirements

### Requirement: Import batch creation
The system SHALL allow an authorized user to upload a supported spreadsheet or CSV file and create a staged import batch without modifying authoritative employee or compensation records.

#### Scenario: Supported file is uploaded
- **WHEN** an authorized user uploads a supported CSV or spreadsheet file
- **THEN** the system SHALL create a staged batch with an identifier, source filename, uploader, creation time, and processing status

#### Scenario: Unsupported file is uploaded
- **WHEN** a user uploads a file format that the system does not support
- **THEN** the system SHALL reject the upload and SHALL not create an import batch

### Requirement: Import validation and preview
The system SHALL validate staged rows for required fields, employee identity, supported compensation types, dates, currency codes, monetary amounts, duplicate identifiers, and overlapping effective periods. The system SHALL provide row-level errors and a preview of accepted changes before confirmation.

#### Scenario: Batch contains invalid rows
- **WHEN** validation finds missing, malformed, duplicate, or conflicting values
- **THEN** the system SHALL mark the affected rows with actionable errors and SHALL keep the batch staged

#### Scenario: Batch passes validation
- **WHEN** all rows pass validation
- **THEN** the system SHALL show the proposed creates and updates and SHALL make the batch eligible for explicit confirmation

### Requirement: Import confirmation and atomic application
The system SHALL require explicit confirmation before applying a staged batch, and application of a confirmed batch MUST be atomic so that authoritative records are not partially updated.

#### Scenario: Valid batch is confirmed
- **WHEN** an authorized user confirms a valid staged batch
- **THEN** the system SHALL apply all accepted employee and compensation changes in one transaction and mark the batch as applied

#### Scenario: Batch application fails
- **WHEN** an error occurs while applying a confirmed batch
- **THEN** the system SHALL roll back all changes from that batch and mark it as failed with an actionable error