# Salary Management API

Spring Boot API for ACME salary management. The application uses Java 17, SQLite, Flyway migrations, and Spring Security.

## Local development

Install Maven 3.9+ or add the Maven Wrapper, then run:

```powershell
mvn spring-boot:run
```

The SQLite database defaults to `backend/data/salary-management.db`. Set `SALARY_DB_PATH` to choose another location. The backend uses Spring Data repositories over the persistence layer, while Flyway owns schema changes.

Back up the database while the application is stopped by copying the SQLite file to a protected `backend/backups/` location or an equivalent organization-managed backup store. Local database and backup files are excluded from source control. Restore by stopping the application, replacing the configured database file, and starting the application so Flyway can verify the schema.

The `V3__development_seed_data.sql` migration supplies a small local dataset across Germany, the United States, and Spain. The CSV under `src/test/resources/fixtures` provides multi-country import test data, including base salary, bonus, and allowance-compatible compensation rows.