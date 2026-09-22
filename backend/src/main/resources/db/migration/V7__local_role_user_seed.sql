-- Local development logins only. Replace or remove this seed for shared environments.
INSERT INTO app_user (username, password_hash, employee_id, enabled)
VALUES
    ('manager', '{bcrypt}$2a$10$ZysntacTSniag8UQd4TFruj3YHSyttiKLgVtLXzq.8o6PgRYhOI2S', NULL, 1),
    ('executive', '{bcrypt}$2a$10$bipeXdEKsLEszOz4JletreVsdP.DIerQFH3gosNDOxzbYpwKdvGEC', NULL, 1),
    ('employee', '{bcrypt}$2a$10$Iue1QH9ud2TQbFRX1AfCPOPZ2be/eZupQRPXgWoMHWQpFHwyX9jmS',
        (SELECT id FROM employee WHERE employee_identifier = 'ACME-10001'), 1);

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM app_user u
CROSS JOIN app_role r
WHERE (u.username = 'manager' AND r.name = 'HR_MANAGER')
   OR (u.username = 'executive' AND r.name = 'HR_EXECUTIVE')
   OR (u.username = 'employee' AND r.name = 'EMPLOYEE');
