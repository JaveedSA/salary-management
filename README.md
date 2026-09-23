# ACME Salary Management

ACME Salary Management is a role-aware application for managing employee profiles, compensation changes, imports, approvals, reporting, and audit history.

It gives HR teams one place to maintain compensation data, validate large employee imports, review changes before they take effect, and understand compensation across the organization.

## What It Provides

### Employee and compensation management

- Maintain employee profiles and organizational information.
- Record base salary, bonus, and allowance changes.
- Track effective dates, currencies, pay frequency, reasons, and approval status.
- Preserve compensation history instead of overwriting past records.

### Controlled imports

- Upload employee and compensation data from CSV files.
- Stage and validate data before changing authoritative records.
- Review rejected rows and row-level validation errors.
- Process large files, including the 10,000-employee sample in `docs`.
- Apply an approved batch as one controlled operation.

### Approvals and audit

- Give HR Managers a queue of pending compensation changes.
- Require a decision reason for approvals, rejections, and reversals.
- Prevent invalid status transitions.
- Record approval and audit events for traceability.

### Reporting

- Filter compensation by country, department, level, type, currency, and effective date.
- View native-currency and normalized-currency results.
- Protect small populations with aggregate suppression.

## Roles

| Role | Primary access |
| --- | --- |
| HR Manager | Manage employees and compensation, review imports, approve changes, view reports and audit history |
| HR Executive | Manage assigned employee and compensation work, review imports, view reports |
| Employee | View permitted personal employee and compensation information |
| Administrator | Local system administration account; salary access is not granted implicitly |

## Main Screens

| Screen | Purpose |
| --- | --- |
| `/dashboard` | Overview and workspace navigation |
| `/employees` | Employee directory, profiles, and compensation history |
| `/imports` | CSV staging, validation, rejected-row review, and application |
| `/reports` | Compensation filters, metrics, and normalized reporting |
| `/approvals` | Pending decisions and audit history |

## System Shape

The application is built as a two-part web application:

- **Frontend:** Angular 17 standalone components and reactive forms.
- **Backend:** Spring Boot 3.3 REST API with Spring Security and Spring Data JPA.
- **Persistence:** SQLite for local development, managed through Flyway migrations.
- **Data controls:** Server-side authorization, validation, effective-dated records, approval status rules, and audit events.

## Local Development

For prerequisites, startup commands, database behavior, local accounts, sample imports, and troubleshooting, see [LOCAL_SETUP.md](LOCAL_SETUP.md).

The local development accounts are:

| Role | Username | Password |
| --- | --- | --- |
| Administrator | `admin` | `AdminLocal123!` |
| HR Manager | `manager` | `ManagerLocal123!` |
| HR Executive | `executive` | `ExecutiveLocal123!` |
| Employee | `employee` | `EmployeeLocal123!` |

These credentials are for local development only.

## Sample Data and Documentation

- [10,000-employee sample](docs/sample-10000-employees.csv)
- [100-row mixed-validity sample](docs/sample-100-employees-mixed.csv)
- [Local setup guide](LOCAL_SETUP.md)

## Quality Checks

The project includes backend unit tests, Angular tests, and Playwright browser workflows covering authentication, role-aware navigation, employee compensation, reporting, approvals, and audit history.

Run the checks described in [LOCAL_SETUP.md](LOCAL_SETUP.md) after starting the required local services.