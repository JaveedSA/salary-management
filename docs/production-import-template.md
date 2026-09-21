# Production Import Template

The production import is a UTF-8 CSV with one header row and one compensation row per employee and effective date. The backend stages the file first; validation and explicit confirmation are required before authoritative records change.

## Columns

The columns must appear in this order:

```text
employee_identifier,full_name,work_email,employment_status,employment_type,hire_date,country,location,department,job_title,job_family,job_level,compensation_type,amount_minor_units,currency_code,pay_frequency,effective_from
```

- `employee_identifier`: required and unique in the employee population.
- `full_name`, `work_email`, `employment_status`, `employment_type`, `hire_date`, and `country`: required employee fields.
- `hire_date` and `effective_from`: ISO dates (`YYYY-MM-DD`).
- `compensation_type`: `BASE_SALARY`, `BONUS`, or `ALLOWANCE`.
- `amount_minor_units`: non-negative integer in the smallest currency unit; no decimal or thousands separators.
- `currency_code`: three-letter ISO 4217 code, for example `USD`, `GBP`, or `INR`.
- `pay_frequency`: required text such as `MONTHLY` or `ANNUAL`.

`location`, `department`, `job_title`, `job_family`, and `job_level` may be blank. Quote fields containing commas and do not include formulas or executable spreadsheet content.

## Policies

- Country and currency are preserved as supplied and validated as required fields.
- Employee and compensation rows are validated for duplicates and overlapping effective periods.
- Imports are staged, reviewed, and explicitly confirmed. HR Executives may review; only HR Managers may apply.
- New imported compensation starts as `PENDING` approval.
- Aggregate reporting suppresses populations below five employees.
- Rejected rows are exported with row number and validation error; rejected source files remain outside authoritative records.

## Approval and rollback

The initial policy requires HR Manager approval for imported compensation before an approved batch is applied. A failed application is rolled back and marked failed. After cutover, corrections use new effective-dated records and audited reversals rather than deleting imported history.
