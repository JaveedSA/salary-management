## Context

ACME needs a web-based compensation system for approximately 10,000 employees across multiple countries. The current source of data is a set of spreadsheets, so the design must support controlled migration, reliable historical salary data, multi-currency reporting, and traceable changes to sensitive records.

The application will have an Angular web client for the HR Manager, a Java Spring Boot backend exposing authenticated REST APIs, and SQLite for the initial persistent store. The initial scope is compensation administration and reporting, not payroll calculation or statutory processing.

## Goals / Non-Goals

**Goals:**

- Provide a single source of truth for employee profiles and effective-dated compensation records.
- Support current, historical, and approved future compensation without overwriting prior records.
- Import spreadsheet and CSV data through a staged validation and review flow.
- Provide reportable dimensions and metrics for country, department, job family, job level, status, currency, and date.
- Preserve audit events for sensitive profile and compensation changes.
- Enforce authenticated access and authorization in the backend, with the UI reflecting permitted operations.
- Keep the design understandable and testable at the initial scale of 10,000 employees.

**Non-Goals:**

- Payroll runs, tax calculation, deductions, payslips, or bank payment processing.
- Storage of bank details, government identifiers, health data, or other unrelated personal information.
- Full natural-language analytics in the first release; reporting will begin with structured filters and transparent calculations.
- Replacing an HRIS as the authoritative source for all employee lifecycle data.

## Decisions

### Angular client and Spring Boot REST API

Use Angular for the browser application and Spring Boot for the backend API. Angular provides a structured client for tables, forms, import review, and reporting views, while Spring Boot provides validation, transaction boundaries, security enforcement, and a clear service layer.

Alternative considered: a server-rendered application. It would reduce frontend setup, but the import preview, filterable employee tables, and reporting workflows benefit from a dedicated client. The API boundary also keeps future integrations possible.

### SQLite as the initial database

Use SQLite with a migration tool and a repository abstraction so the initial application is easy to run and deploy. The expected dataset of 10,000 employees plus history and audit events is within SQLite's practical range for a single-organization deployment.

Alternative considered: PostgreSQL from the start. PostgreSQL would be the stronger default for multi-instance production, concurrent writes, and organizational growth, but SQLite keeps the initial deployment simple. The persistence layer must avoid SQLite-specific behavior leaking into domain services so a later move to PostgreSQL remains viable.

### Separate employee, compensation, import, approval, and audit records

Store employee identity and organizational attributes separately from compensation records. Compensation records contain amount, currency, pay frequency, effective start and end dates, reason, and status. Import batches and rows are staged before they modify authoritative records. Audit events store actor, timestamp, entity, action, old value, new value, and reason.

This prevents salary history from being destroyed by edits and allows an import to be reviewed or rejected without partially changing live data.

### Effective-date invariants

For each employee and compensation type, authoritative records must not overlap. A new record closes or follows the prior period according to its effective date, and future records remain distinguishable from currently effective records. The service layer will validate these invariants inside a transaction rather than relying only on client-side checks.

### Integer minor units and explicit currency metadata

Persist monetary amounts as integer minor units with an ISO currency code and pay frequency. Reports that normalize currencies must record the FX rate source and effective date used for the calculation. Native compensation values remain unchanged when a reporting conversion is performed.

Alternative considered: floating-point amounts. Floating-point storage can introduce rounding errors in salary totals and comparisons, so it is not suitable for authoritative monetary values.

### Staged imports with deterministic validation

The backend will accept CSV and common spreadsheet input, create an import batch, map recognized columns, and validate each row against employee identity, dates, currencies, amounts, and duplicate or overlapping records. The Angular client will show row-level errors and a preview. Only an explicitly confirmed batch can create or update authoritative records.

### Backend authorization and privacy-aware reporting

Spring Security will protect API routes, and authorization checks will be applied in the backend service/query layer rather than only in Angular. Reporting queries will return aggregate results and employee counts; configurable small-group suppression should be supported before exposing reports that could identify an individual salary.

## Risks / Trade-offs

- [SQLite write concurrency and multi-instance deployment] -> Start with a single application instance and documented backup strategy; keep repositories portable and define PostgreSQL migration as a scale-out path.
- [Spreadsheet columns and data quality vary between countries] -> Use import mapping, row-level validation, preview, rejected-row export, and batch-level rollback.
- [Currency conversion can make reports misleading] -> Preserve native amounts, display conversion assumptions, and store FX source/date with normalized report results.
- [Incorrect effective dates can corrupt historical reporting] -> Enforce non-overlapping periods transactionally and require explicit handling of retroactive corrections.
- [Compensation data is highly sensitive] -> Apply backend authorization, minimize stored personal data, audit reads and writes where required, and suppress small groups in aggregate reports.
- [Direct salary edits may require organizational approval] -> Model approval state and approval events from the start, while allowing the initial policy to be configured for a single HR Manager.

## Migration Plan

1. Define the accepted import template and required columns for employee and compensation data.
2. Load representative exports into staging and resolve duplicate identifiers, missing currencies, invalid dates, and overlapping salary periods.
3. Run a pilot import for a small country or department and reconcile employee counts and salary totals against the source spreadsheets.
4. Import the remaining data as reviewed batches, retaining the source batch identifier in audit metadata.
5. Freeze spreadsheet edits during the final cutover, perform a reconciliation, and make the application the operational source of truth.
6. Retain original spreadsheet files outside the application according to ACME's retention policy; do not treat them as live records.

Rollback consists of rejecting or reversing an uncommitted import batch. After cutover, corrections should be made through new effective-dated records and audited adjustments rather than deleting imported history.

## Open Questions

- Will SQLite run as a single-instance deployment, or is immediate horizontal scaling required?
- What authentication provider and organization identity system will ACME use?
- Which roles besides HR Manager need access, and how should access be scoped by country, department, or business unit?
- Which currencies and FX rate source should be supported in the first release?
- What are the required salary types: base salary only, or also bonuses and allowances?
- What minimum group size should suppress an aggregate report to protect privacy?
- Should approval be required for every change, only imported batches, or configurable by amount and organizational scope?