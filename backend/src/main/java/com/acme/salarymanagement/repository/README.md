# Repository layer

Spring Data repository interfaces for the domain entities belong in this package. Keep database-specific details behind these interfaces so the service layer can move from the initial SQLite deployment to PostgreSQL without changing business rules.