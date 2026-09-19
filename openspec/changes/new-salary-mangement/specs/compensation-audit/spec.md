## ADDED Requirements

### Requirement: Compensation change audit trail
The system SHALL record an audit event for every created, updated, approved, rejected, or reversed employee or compensation record. Each event SHALL include actor, timestamp, entity, action, previous value when applicable, new value when applicable, reason, source, and related import or approval identifier when applicable.

#### Scenario: User changes compensation
- **WHEN** an authorized user creates or updates an employee compensation record
- **THEN** the system SHALL persist an audit event containing the old and new values and the effective date

#### Scenario: User reverses a compensation change
- **WHEN** an authorized user reverses a compensation change
- **THEN** the system SHALL create a new reversal event and SHALL retain the original event and record

### Requirement: Audit history access
The system SHALL allow only users with audit access to view audit events, and audit events MUST NOT be editable or deleted through normal application operations.

#### Scenario: Authorized user views audit history
- **WHEN** a user with audit access requests an employee's audit history
- **THEN** the system SHALL return the chronological events for that employee and related compensation records

#### Scenario: Unauthorized user requests audit history
- **WHEN** a user without audit access requests audit events
- **THEN** the system SHALL deny the request without exposing event contents

### Requirement: Approval state tracking
The system SHALL track approval status and approval events for compensation changes and import batches, including pending, approved, rejected, actor, timestamp, and decision reason.

#### Scenario: Compensation change is approved
- **WHEN** an authorized approver approves a pending change
- **THEN** the system SHALL record the approval event and mark the change approved without removing its prior pending history

#### Scenario: Compensation change is rejected
- **WHEN** an authorized approver rejects a pending change with a reason
- **THEN** the system SHALL mark the change rejected and SHALL retain the rejection reason in the audit history