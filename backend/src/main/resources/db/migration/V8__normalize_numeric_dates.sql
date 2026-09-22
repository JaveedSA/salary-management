-- Repair dates written as epoch milliseconds by earlier imports.
UPDATE employee
SET hire_date = date(CAST(hire_date AS INTEGER) / 1000, 'unixepoch')
WHERE hire_date GLOB '[0-9]*' AND hire_date NOT GLOB '*[^0-9]*';

UPDATE employee
SET termination_date = date(CAST(termination_date AS INTEGER) / 1000, 'unixepoch')
WHERE termination_date GLOB '[0-9]*' AND termination_date NOT GLOB '*[^0-9]*';

UPDATE compensation_record
SET effective_from = date(CAST(effective_from AS INTEGER) / 1000, 'unixepoch')
WHERE effective_from GLOB '[0-9]*' AND effective_from NOT GLOB '*[^0-9]*';

UPDATE compensation_record
SET effective_until = date(CAST(effective_until AS INTEGER) / 1000, 'unixepoch')
WHERE effective_until GLOB '[0-9]*' AND effective_until NOT GLOB '*[^0-9]*';