-- Bound the development-only seeded bonus so later local bonus periods can be tested.
UPDATE compensation_record
SET effective_until = '2026-09-28'
WHERE employee_id = (SELECT id FROM employee WHERE employee_identifier = 'ACME-10001')
  AND compensation_type = 'BONUS'
  AND effective_from = '2026-01-01'
  AND reason = 'Development seed'
  AND effective_until IS NULL;
