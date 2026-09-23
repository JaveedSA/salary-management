-- flyway:executeInTransaction=false
PRAGMA foreign_keys = OFF;

CREATE TABLE import_batch_new (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_filename TEXT NOT NULL,
    uploaded_by INTEGER NOT NULL REFERENCES app_user(id),
    status TEXT NOT NULL CHECK (status IN ('STAGED', 'VALIDATED', 'PENDING', 'APPROVED', 'APPLIED', 'FAILED', 'REJECTED', 'REVERSED')),
    total_rows INTEGER NOT NULL DEFAULT 0,
    accepted_rows INTEGER NOT NULL DEFAULT 0,
    rejected_rows INTEGER NOT NULL DEFAULT 0,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    applied_at TEXT
);

INSERT INTO import_batch_new (
    id, source_filename, uploaded_by, status, total_rows, accepted_rows, rejected_rows, created_at, applied_at
)
SELECT id, source_filename, uploaded_by, status, total_rows, accepted_rows, rejected_rows, created_at, applied_at
FROM import_batch;

DROP TABLE import_batch;
ALTER TABLE import_batch_new RENAME TO import_batch;

PRAGMA foreign_keys = ON;
