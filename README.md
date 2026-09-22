# salary-management

ACME salary-management is an Angular 17 frontend backed by a Spring Boot 3.3 API and a local SQLite database. The backend owns authentication, authorization, validation, Flyway migrations, imports, reporting, approvals, and audit history.

## Prerequisites

- Java 17
- Maven 3.8 or later
- Node.js and npm compatible with Angular CLI 17
- PowerShell on Windows, or equivalent shell commands on another operating system

Maven must be available on the developer's `PATH`. The commands below run from the `backend` directory and use the repository's standard Maven project configuration.

## Start the backend and database

Open a terminal in the repository root and create the local database directory:

```powershell
New-Item -ItemType Directory -Force backend/data | Out-Null
```

Run backend tests:

```powershell
Push-Location backend
mvn test
Pop-Location
```

Start the API. Flyway creates and upgrades the SQLite database automatically on startup:

```powershell
Push-Location backend
mvn spring-boot:run
```

The default database file is `backend/data/salary-management.db`. The default API URL is `http://localhost:8080`. Verify that the application is running with:

```powershell
Invoke-WebRequest http://localhost:8080/actuator/health
```

The schema migrations are in `backend/src/main/resources/db/migration`. They create the employee, compensation, import, approval, audit, user, and role tables, add indexes, and insert development employee and compensation records. Do not edit an existing migration after it has been applied; add a new `V{number}__description.sql` migration instead.

## Database configuration

The backend reads these environment variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `SALARY_DB_PATH` | `./data/salary-management.db` when running from `backend` | SQLite database path |
| `SERVER_PORT` | `8080` | Spring Boot API port |

Example using a database outside the source tree:

```powershell
$env:SALARY_DB_PATH = 'D:\salary-data\salary-management.db'
$env:SERVER_PORT = '8081'
mvn spring-boot:run
```

SQLite is the only runtime database configured by this project. Stop the backend before copying the database so that no write is in progress.

## Inspect, back up, and restore SQLite

If the SQLite command-line tool is installed, inspect the schema and migration history with:

```powershell
sqlite3 backend/data/salary-management.db ".tables"
sqlite3 backend/data/salary-management.db "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
sqlite3 backend/data/salary-management.db "SELECT employee_identifier, full_name FROM employee ORDER BY employee_identifier;"
```

Back up the database while the backend is stopped:

```powershell
New-Item -ItemType Directory -Force backups | Out-Null
Copy-Item backend/data/salary-management.db "backups/salary-management-$(Get-Date -Format yyyyMMdd-HHmmss).db"
```

Restore a backup while the backend is stopped:

```powershell
Copy-Item backups/salary-management-YYYYMMDD-HHMMSS.db backend/data/salary-management.db -Force
```

Keep backups outside `backend/data`, protect them as compensation data, and test a restore before relying on a backup for recovery. Flyway checks the restored database and applies any newer migrations when the backend starts.

## Start the frontend

In a second terminal:

```powershell
Push-Location frontend
npm install
npm start
```

The Angular development server is available at `http://localhost:4200`. Build the production bundle with:

```powershell
npm run build
```

Frontend services use relative `/api/...` URLs. The current Angular configuration does not include a development proxy or CORS configuration, so use the frontend with the same-origin deployment setup, or add a local reverse proxy that forwards `/api` to `http://localhost:8080` before using the standalone Angular dev server for browser workflows.

## Authentication and application workflow

The backend enforces authorization independently of Angular. HR Managers can manage compensation, apply imports, approve decisions, view audit history, and view reports. HR Executives can manage assigned employee and compensation work, review imports, and view reports but cannot apply imports or approve decisions. Employees can read their own permitted records. Admin is reserved for system administration and does not receive salary access implicitly.

Development seed data creates employees, compensation records, and local-only accounts for each role. After Flyway runs, use these credentials locally:

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `AdminLocal123!` |
| HR Manager | `manager` | `ManagerLocal123!` |
| HR Executive | `executive` | `ExecutiveLocal123!` |
| Employee | `employee` | `EmployeeLocal123!` |

The employee account is linked to `ACME-10001`. Do not use these seeded passwords outside local development; replace or remove migrations V6 and V7 for shared environments.

Use `/reports` for filtered native and normalized compensation reporting, and `/approvals` for compensation decisions and audit timelines. Aggregate populations below five employees are suppressed.

## Import the 10,000-employee sample

The generated sample file is [docs/sample-10000-employees.csv](docs/sample-10000-employees.csv). It contains 10,000 unique employees and one base-salary row per employee. The production contract and approval policy are documented in [docs/production-import-template.md](docs/production-import-template.md).

The supported workflow is:

1. Upload the CSV to create a staged batch.
2. Validate the batch and review row-level errors and counts.
3. Obtain HR Manager approval for the pending compensation changes.
4. Explicitly apply the approved batch.
5. Reconcile employee counts, currencies, totals, and effective dates before treating the database as authoritative.

Uploads are staged before authoritative records change. Rejected rows can be exported without modifying employee or compensation records. A failed application rolls back the transaction and marks the batch failed. After cutover, correct data with a new effective-dated record or an audited reversal; do not delete imported history.

## Frontend and backend tests

Run backend tests from `backend`:

```powershell
mvn test
```

Run Angular tests from `frontend`:

```powershell
npm test -- --watch=false --browsers=ChromeHeadless
```

Run browser workflow tests with the Angular server running in another terminal:

```powershell
npm run e2e
```

The Playwright workflows mock API responses and cover login, role-aware navigation, employee compensation, reporting, approval, and audit history. Install a local Chrome browser before running them.