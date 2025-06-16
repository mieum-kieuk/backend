INSERT INTO member (member_id, login_id, password, name, phone_number, email, authority, agree_to_receive_sms, agree_to_receive_mail, is_email_verified, created_at, modified_at)
VALUES (1, 'member1', '{noop}test1234!', '회원', '01012345678', 'member1@example.com', 'ROLE_USER', TRUE, TRUE, FALSE, NOW(), NOW());

INSERT INTO admin (admin_id, login_id, password, name, email, is_authorized, authority, created_at, modified_at)
VALUES (1, 'admin1', '{noop}admin1234!', '관리자', 'admin1@example.com', TRUE, 'ROLE_ADMIN', NOW(), NOW());

INSERT INTO admin (admin_id, login_id, password, name, email, is_authorized, authority, created_at, modified_at)
VALUES (2, 'admin2', '{noop}admin1234!', '관리자', 'admin2@example.com', FALSE, 'ROLE_ANONYMOUS', NOW(), NOW());
