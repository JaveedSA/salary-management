-- Repair import timestamps written as epoch milliseconds by SQLite/Hibernate.
UPDATE import_batch
SET created_at = datetime(CAST(created_at AS INTEGER) / 1000, 'unixepoch')
WHERE created_at GLOB '[0-9]*' AND created_at NOT GLOB '*[^0-9]*';