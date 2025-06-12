INSERT INTO admin (login_id, password, name, email, is_authorized, authority, created_at, modified_at)
VALUES ('admin1', '{noop}admin1234!', '관리자', 'admin1@example.com', TRUE, 'ROLE_ADMIN', NOW(), NOW());
