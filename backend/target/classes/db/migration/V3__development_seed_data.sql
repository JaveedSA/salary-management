INSERT INTO employee (
    employee_identifier, full_name, work_email, employment_status, employment_type,
    hire_date, country, location, legal_entity, department, business_unit,
    job_title, job_family, job_level, cost_center
) VALUES
    ('ACME-10001', 'Maya Chen', 'maya.chen@acme.example', 'ACTIVE', 'FULL_TIME',
     '2021-06-14', 'DE', 'Berlin', 'ACME GmbH', 'Engineering', 'Product',
     'Senior Software Engineer', 'Software Engineering', 'L5', 'ENG-DE-04'),
    ('ACME-10002', 'Noah Williams', 'noah.williams@acme.example', 'ACTIVE', 'FULL_TIME',
     '2020-02-03', 'US', 'New York', 'ACME Inc.', 'Finance', 'Corporate',
     'Financial Analyst', 'Finance', 'L4', 'FIN-US-02'),
    ('ACME-10003', 'Sofia Garcia', 'sofia.garcia@acme.example', 'ACTIVE', 'FULL_TIME',
     '2022-09-19', 'ES', 'Madrid', 'ACME Iberia S.L.', 'People', 'Corporate',
     'HR Business Partner', 'Human Resources', 'L4', 'HR-ES-01');

INSERT INTO compensation_record (
    employee_id, compensation_type, amount_minor_units, currency_code, pay_frequency,
    effective_from, effective_until, reason, status
)
SELECT id, 'BASE_SALARY', 9200000, 'EUR', 'ANNUAL', '2026-01-01', NULL, 'Development seed', 'APPROVED'
FROM employee WHERE employee_identifier = 'ACME-10001';

INSERT INTO compensation_record (
    employee_id, compensation_type, amount_minor_units, currency_code, pay_frequency,
    effective_from, effective_until, reason, status
)
SELECT id, 'BONUS', 920000, 'EUR', 'ANNUAL', '2026-01-01', NULL, 'Development seed', 'APPROVED'
FROM employee WHERE employee_identifier = 'ACME-10001';

INSERT INTO compensation_record (
    employee_id, compensation_type, amount_minor_units, currency_code, pay_frequency,
    effective_from, effective_until, reason, status
)
SELECT id, 'ALLOWANCE', 15000, 'EUR', 'MONTHLY', '2026-01-01', NULL, 'Development seed', 'APPROVED'
FROM employee WHERE employee_identifier = 'ACME-10001';

INSERT INTO compensation_record (
    employee_id, compensation_type, amount_minor_units, currency_code, pay_frequency,
    effective_from, effective_until, reason, status
)
SELECT id, 'BASE_SALARY', 11000000, 'USD', 'ANNUAL', '2026-01-01', NULL, 'Development seed', 'APPROVED'
FROM employee WHERE employee_identifier = 'ACME-10002';

INSERT INTO compensation_record (
    employee_id, compensation_type, amount_minor_units, currency_code, pay_frequency,
    effective_from, effective_until, reason, status
)
SELECT id, 'BASE_SALARY', 5400000, 'EUR', 'ANNUAL', '2026-01-01', NULL, 'Development seed', 'APPROVED'
FROM employee WHERE employee_identifier = 'ACME-10003';