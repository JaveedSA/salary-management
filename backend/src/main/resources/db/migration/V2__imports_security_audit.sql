CREATE TABLE app_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    employee_id INTEGER REFERENCES employee(id),
    enabled INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE app_role (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE user_role (
    user_id INTEGER NOT NULL REFERENCES app_user(id),
    role_id INTEGER NOT NULL REFERENCES app_role(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE import_batch (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_filename TEXT NOT NULL,
    uploaded_by INTEGER NOT NULL REFERENCES app_user(id),
    status TEXT NOT NULL CHECK (status IN ('STAGED', 'VALIDATED', 'APPLIED', 'FAILED', 'REJECTED')),
    total_rows INTEGER NOT NULL DEFAULT 0,
    accepted_rows INTEGER NOT NULL DEFAULT 0,
    rejected_rows INTEGER NOT NULL DEFAULT 0,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    applied_at TEXT
);

CREATE TABLE import_row (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    import_batch_id INTEGER NOT NULL REFERENCES import_batch(id),
    row_number INTEGER NOT NULL,
    raw_data TEXT NOT NULL,
    validation_status TEXT NOT NULL CHECK (validation_status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    validation_errors TEXT,
    UNIQUE (import_batch_id, row_number)
);

CREATE TABLE approval_event (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    import_batch_id INTEGER REFERENCES import_batch(id),
    compensation_record_id INTEGER REFERENCES compensation_record(id),
    actor_user_id INTEGER NOT NULL REFERENCES app_user(id),
    decision TEXT NOT NULL CHECK (decision IN ('PENDING', 'APPROVED', 'REJECTED', 'REVERSED')),
    decision_reason TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_event (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    actor_user_id INTEGER REFERENCES app_user(id),
    entity_type TEXT NOT NULL,
    entity_id INTEGER NOT NULL,
    action TEXT NOT NULL,
    previous_value TEXT,
    new_value TEXT,
    reason TEXT,
    source TEXT,
    import_batch_id INTEGER REFERENCES import_batch(id),
    approval_event_id INTEGER REFERENCES approval_event(id),
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_role (name) VALUES
    ('HR_MANAGER'),
    ('HR_EXECUTIVE'),
    ('EMPLOYEE'),
    ('ADMIN');

CREATE INDEX idx_import_rows_batch ON import_row(import_batch_id, validation_status);
CREATE INDEX idx_audit_entity ON audit_event(entity_type, entity_id, created_at);
CREATE INDEX idx_approval_compensation ON approval_event(compensation_record_id, created_at);