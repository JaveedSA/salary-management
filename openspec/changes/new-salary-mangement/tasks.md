## 1. Project and persistence foundation

- [x] 1.1 Create the Spring Boot backend and Angular frontend project structure with local development configuration.
- [x] 1.2 Add SQLite connectivity, schema migration tooling, repository configuration, and a documented backup location.
- [x] 1.3 Define the database schema and domain models for employees, compensation records, import batches and rows, approvals, users, roles, and audit events.
- [x] 1.4 Add seed data and test fixtures for employees across multiple countries, currencies, departments, and compensation types.

## 2. Authentication and authorization

- [x] 2.1 Configure Spring Security authentication, protected API routes, generic authentication failures, logout, and disabled-account handling.
- [x] 2.2 Implement role and permission models for HR Manager, HR Executive, Employee, and Admin, including scoped access rules.
- [x] 2.3 Enforce authorization in backend services and queries for employee, compensation, import, reporting, audit, and administration operations.
- [x] 2.4 Implement Angular login, logout, session handling, route guards, and role-aware navigation without relying on the client for security.
- [x] 2.5 Add authorization tests covering full HR Manager access, limited HR Executive edits, employee self-access, and Admin system management without implicit salary access.

## 3. Employee and compensation management

- [x] 3.1 Implement employee profile create, update, search, and detail APIs with unique identifier validation.
- [x] 3.2 Implement effective-dated compensation records using integer minor units, ISO currency codes, pay frequency, reason, status, and approval metadata.
- [x] 3.3 Implement separate base salary, bonus, and allowance compensation types and enforce non-overlapping periods per employee and type transactionally.
- [x] 3.4 Implement current, historical, and future compensation queries with clear status and effective-date ordering.
- [x] 3.5 Build Angular employee list, profile form, compensation form, compensation history, and future-change views.
- [x] 3.6 Add backend and frontend validation for required fields, monetary values, currencies, dates, compensation types, and overlapping periods.

## 4. Import and reconciliation

- [x] 4.1 Implement CSV and supported spreadsheet upload endpoints that create staged import batches without modifying authoritative records.
- [x] 4.2 Implement column mapping and row validation for employee identity, compensation types, dates, currencies, amounts, duplicates, and overlaps.
- [x] 4.3 Implement import preview, row-level errors, accepted/rejected row counts, rejected-row export, and batch status transitions.
- [x] 4.4 Implement explicit confirmation and atomic application of valid import batches with rollback on failure.
- [x] 4.5 Build Angular import upload, mapping, validation, preview, confirmation, and failure-recovery workflows.
- [x] 4.6 Run a pilot import fixture and reconcile employee counts, compensation totals, currencies, and effective periods against the source data.

## 5. Compensation reporting

- [x] 5.1 Implement report query filters for country, location, legal entity, department, business unit, job family, job level, employment status, employment type, currency, compensation type, and effective date.
- [x] 5.2 Implement employee count, total, average, median, minimum, maximum, and period-change metrics for selected compensation types.
- [x] 5.3 Implement explicit combined metrics for selected base salary, bonus, and allowance types without silently mixing types.
- [x] 5.4 Implement currency normalization with recorded reporting currency, FX rate source, and FX rate date while preserving native amounts.
- [x] 5.5 Implement configured small-group privacy suppression and ensure aggregate-only reports do not expose individual salary values.
- [x] 5.6 Build Angular reporting filters, summary tables, charts or comparison views, calculation context, and permitted export behavior.
- [x] 5.7 Add reporting tests for filtered populations, invalid dates, multi-currency conversion, missing FX data, selected compensation types, and privacy thresholds.

## 6. Audit and approvals

- [x] 6.1 Implement immutable audit event creation for employee, compensation, import, approval, rejection, and reversal actions.
- [x] 6.2 Record actor, timestamp, entity, action, old and new values, reason, source, and related batch or approval identifiers.
- [x] 6.3 Implement pending, approved, rejected, and reversed states for compensation changes and import batches with decision reasons.
- [x] 6.4 Implement audit-history APIs and restrict access to users with audit permission.
- [x] 6.5 Build Angular approval queue, decision form, employee audit timeline, and permission-aware audit views.
- [x] 6.6 Add tests proving audit events are retained, non-editable through normal operations, and created for approvals and reversals.

## 7. Migration, operations, and quality

- [x] 7.1 Define the production import template, required fields, country/currency rules, privacy threshold, and initial approval policy.
- [x] 7.2 Add database indexes and query tests for employee lookup, effective-date selection, reporting dimensions, and audit history at the 10,000-employee target.
- [x] 7.3 Add API, service, repository, and Angular component tests for the acceptance scenarios in all capability specifications.
- [ ] 7.4 Add end-to-end tests covering login, role restrictions, employee compensation changes, import confirmation, reporting, approval, and audit history.
- [x] 7.5 Document local setup, SQLite backup and restore, configuration, supported import formats, authorization model, and cutover rollback procedure.
- [ ] 7.6 Perform a staged pilot and final spreadsheet migration, reconcile results, and record the cutover evidence before making the application the operational source of truth.