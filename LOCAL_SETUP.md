# Local Setup

This guide runs the ACME salary-management application locally.

The application contains:

- Angular 17 frontend
- Spring Boot 3.3 backend
- Java 17 runtime
- SQLite database
- Flyway database migrations

## Prerequisites

Install and verify:

- Java 17
- Maven 3.8 or later
- Node.js and npm compatible with Angular 17
- Git

PowerShell checks:

```powershell
java -version
mvn -version
node --version
npm --version
git --version
```

Maven must be available on `PATH`. This repository does not currently contain a Maven wrapper.

## Install Frontend Dependencies

From the repository root:

```powershell
Push-Location frontend
npm install
Pop-Location
```

## Create and Start the Backend

Create the local data directory. This creates the directory only; SQLite creates the database file when the backend starts.

```powershell
New-Item -ItemType Directory -Force backend/data | Out-Null
```

Start the backend in its own terminal:

```powershell
Push-Location backend
mvn spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

The default database file is:

```text
backend/data/salary-management.db
```

On first startup, Flyway runs the migrations in:

```text
backend/src/main/resources/db/migration
```

Flyway creates the tables, indexes, roles, development employees, and local development users automatically. Do not run the migration files manually.

Check the health endpoint from another terminal:

```powershell
Invoke-WebRequest http://localhost:8080/actuator/health
```

## Start the Frontend

Open a second terminal:

```powershell
Push-Location frontend
npm start
```

The Angular development server runs at:

```text
http://localhost:4200
```

The frontend uses relative `/api/...` URLs. For browser use, the frontend must be served through the same-origin setup that forwards `/api` requests to the backend at `http://localhost:8080`. If the standalone Angular server is used without that forwarding configuration, the page may load while API requests fail.

## Local Login Accounts

These accounts are inserted by the development seed migrations:

| Role | Username | Password |
| --- | --- | --- |
| Administrator | `admin` | `AdminLocal123!` |
| HR Manager | `manager` | `ManagerLocal123!` |
| HR Executive | `executive` | `ExecutiveLocal123!` |
| Employee | `employee` | `EmployeeLocal123!` |

The employee account is linked to employee `ACME-10001`.

These credentials are for local development only. Replace or remove the development seed accounts before using the application in a shared or production environment.

## Main Workflows

After signing in:

- `/employees`: employee directory, profiles, and compensation history/entry
- `/imports`: CSV staging, validation, rejected-row review, and applying imports
- `/reports`: compensation filters, aggregates, and normalized reporting
- `/approvals`: pending compensation approvals and audit history

The HR Manager account has the broadest local workflow permissions.

## Sample Import Files

Sample files are available under `docs`:

- `docs/sample-100-employees-mixed.csv`: 100 rows containing valid and intentionally rejected data
- `docs/sample-10000-employees.csv`: 10,000 valid employee/compensation rows

The normal import workflow is:

1. Upload the CSV.
2. Validate the staged batch.
3. Review rejected rows and validation errors.
4. Obtain approval when required.
5. Apply the approved batch.

## Useful Configuration

Run the backend with a different SQLite file or port:

```powershell
$env:SALARY_DB_PATH = 'D:\salary-data\salary-management.db'
$env:SERVER_PORT = '8081'
Push-Location backend
mvn spring-boot:run
Pop-Location
```

Available backend variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `SALARY_DB_PATH` | `./data/salary-management.db` from `backend` | SQLite database path |
| `SERVER_PORT` | `8080` | Backend HTTP port |

## Reset the Local Database

Stop the backend first. Then remove the local database file:

```powershell
Remove-Item backend/data/salary-management.db -ErrorAction SilentlyContinue
```

Start the backend again. SQLite creates a new file and Flyway runs the complete migration set from the beginning.

Do not remove a database that contains data you need. Make a backup first:

```powershell
New-Item -ItemType Directory -Force backups | Out-Null
Copy-Item backend/data/salary-management.db "backups/salary-management-$(Get-Date -Format yyyyMMdd-HHmmss).db"
```

## Validation Commands

Backend tests:

```powershell
Push-Location backend
mvn test
Pop-Location
```

Frontend production build:

```powershell
Push-Location frontend
npm run build
Pop-Location
```

Frontend tests:

```powershell
Push-Location frontend
npm test -- --watch=false --browsers=ChromeHeadless
Pop-Location
```

Playwright browser tests:

```powershell
Push-Location frontend
npm run e2e
Pop-Location
```

## Troubleshooting

### `mvn` is not recognized

Install Maven and add its `bin` directory to `PATH`, then open a new terminal and run `mvn -version`.

### The database file does not appear

Make sure the backend started successfully and that the current directory for `mvn spring-boot:run` was `backend`. The default relative path is resolved from that directory.

### Port 8080 or 4200 is already in use

Use `SERVER_PORT` for the backend. For Angular, start with a different port:

```powershell
Push-Location frontend
npm start -- --port 4300
Pop-Location
```

### Database migration errors occur

Stop the backend and check the configured `SALARY_DB_PATH`. For a disposable local database, delete the database file and restart the backend. Do not edit an already-applied Flyway migration; add a new versioned migration instead.

### SQLite CLI is unavailable

The application does not require the `sqlite3` command-line tool. It is optional and is only needed for direct database inspection.
