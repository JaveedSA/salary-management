-- Local development login only. Replace or remove this seed for shared environments.
INSERT INTO app_user (username, password_hash, enabled)
VALUES ('admin', '{bcrypt}$2a$10$YLcDpbF0xE.Tmu98r3zB4e.yj6XD2ScuHZTWy8Zjl33SYw4O4QpmS', 1);

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM app_user u
CROSS JOIN app_role r
WHERE u.username = 'admin' AND r.name = 'ADMIN';
