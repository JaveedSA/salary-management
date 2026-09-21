CREATE INDEX idx_employee_identifier_lookup
    ON employee(employee_identifier);

CREATE INDEX idx_employee_reporting_dimensions
    ON employee(country, location, legal_entity, department, business_unit, job_family, job_level,
        employment_status, employment_type);

CREATE INDEX idx_compensation_reporting_dimensions
    ON compensation_record(compensation_type, currency_code, effective_from, effective_until, employee_id);

CREATE INDEX idx_import_batch_status_created
    ON import_batch(status, created_at);
