# Cutover Evidence

Status: **Blocked before live database execution**

Date: 2026-09-22

This document records the available source-file baseline and the validation blocker for OpenSpec task 7.6. It does not claim that the application database became the operational source of truth.

## Source Baseline

Source file: [sample-10000-employees.csv](sample-10000-employees.csv)

The source fixture was parsed with PowerShell `Import-Csv`:

| Measure | Result |
| --- | ---: |
| Data rows | 10,000 |
| Unique employees | 10,000 |
| Compensation rows | 10,000 |
| Total amount in minor units | 64,375,000,000 |
| Effective date | 2026-01-01 for all rows |

Currency distribution:

| Currency | Rows |
| --- | ---: |
| AUD | 1,250 |
| CAD | 1,250 |
| EUR | 1,250 |
| GBP | 1,250 |
| INR | 1,250 |
| JPY | 1,250 |
| SGD | 1,250 |
| USD | 1,250 |

## Intended Pilot and Final Reconciliation

The live evidence still required is:

1. Stage the fixture through the running API.
2. Validate the staged batch and record accepted and rejected row counts.
3. Approve and explicitly apply the batch against an isolated SQLite database.
4. Query the resulting database and compare employee count, compensation row count, currency distribution, total minor-unit amounts, effective dates, batch status, and audit events against the source baseline.
5. Preserve the final batch identifier and database backup hash, then document rollback readiness.

## Blocker

The backend could not be started in this environment:

- `mvn` is not available on `PATH`.
- A cached Maven 3.9.16 executable was available, but the cached file `spring-boot-starter-parent-3.3.5.pom` contains an HTML Artifactory error page instead of a Maven POM.
- `repo.maven.apache.org` did not resolve from the environment, so the dependency could not be repaired.
- No temporary cutover database was created and no credentials or production data were used.

OpenSpec task 7.6 remains unchecked until the live staged pilot and final database reconciliation can be executed successfully.
