CREATE TABLE employee (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_identifier TEXT NOT NULL UNIQUE,
    full_name TEXT NOT NULL,
    work_email TEXT NOT NULL UNIQUE,
    employment_status TEXT NOT NULL,
    employment_type TEXT NOT NULL,
    hire_date TEXT NOT NULL,
    termination_date TEXT,
    country TEXT NOT NULL,
    location TEXT,
    legal_entity TEXT,
    department TEXT,
    business_unit TEXT,
    job_title TEXT,
    job_family TEXT,
    job_level TEXT,
    manager_identifier TEXT,
    cost_center TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE compensation_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_id INTEGER NOT NULL REFERENCES employee(id),
    compensation_type TEXT NOT NULL CHECK (compensation_type IN ('BASE_SALARY', 'BONUS', 'ALLOWANCE')),
    amount_minor_units INTEGER NOT NULL CHECK (amount_minor_units >= 0),
    currency_code TEXT NOT NULL,
    pay_frequency TEXT NOT NULL,
    effective_from TEXT NOT NULL,
    effective_until TEXT,
    reason TEXT,
    status TEXT NOT NULL DEFAULT 'PENDING',
    approval_reason TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (employee_id, compensation_type, effective_from)
);

CREATE INDEX idx_compensation_employee_dates
    ON compensation_record(employee_id, compensation_type, effective_from, effective_until);