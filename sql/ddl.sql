-- 회원 등급
DROP TABLE IF EXISTS membership CASCADE;
CREATE TABLE membership
(
    membership_id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(30) NOT NULL,
    point_rate        INT UNSIGNED NOT NULL,
    level             INT UNSIGNED NOT NULL,
    is_default        BOOLEAN     NOT NULL DEFAULT 0,
    max_benefit_point INT UNSIGNED NOT NULL,
    min_amount_spent  BIGINT UNSIGNED NOT NULL,
    created_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    CONSTRAINT UQ_MEMBERSHIP_NAME UNIQUE (name)
);

-- 기본 멤버십 삽입
INSERT INTO membership (name, point_rate, level, is_default, max_benefit_point, min_amount_spent)
VALUES ('BASIC', 1, 1, 1, 100000, 500000);

-- 회원
DROP TABLE IF EXISTS member CASCADE;
CREATE TABLE member
(
    member_id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    membership_id         TINYINT UNSIGNED NOT NULL,
    login_id              VARCHAR(20)  NOT NULL,
    password              VARCHAR(255) NOT NULL,
    name                  VARCHAR(30)  NOT NULL,
    phone_number          VARCHAR(11)  NOT NULL,
    email                 VARCHAR(100) NOT NULL,
    authority             ENUM('ROLE_ANONYMOUS', 'ROLE_USER') NOT NULL,
    agree_to_receive_sms  BOOLEAN NOT NULL,
    agree_to_receive_mail BOOLEAN NOT NULL,
    is_email_verified     BOOLEAN NOT NULL,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP,
    deleted_at            TIMESTAMP,
    CONSTRAINT UQ_MEMBER_LOGIN_ID UNIQUE (login_id),
    CONSTRAINT UQ_MEMBER_PHONE_NUMBER UNIQUE (phone_number),
    CONSTRAINT UQ_MEMBER_EMAIL UNIQUE (email)
);

-- 배송지
DROP TABLE IF EXISTS delivery CASCADE;
CREATE TABLE delivery
(
    delivery_id    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id      BIGINT UNSIGNED NOT NULL,
    delivery_name  VARCHAR(50)  NOT NULL,
    recipient_name VARCHAR(30)  NOT NULL,
    zip_code       VARCHAR(5)   NOT NULL,
    basic_address  VARCHAR(100) NOT NULL,
    detail_address VARCHAR(100),
    phone_number   VARCHAR(11)  NOT NULL,
    is_default     BOOLEAN NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP
);

-- 적립 적립금
DROP TABLE IF EXISTS saved_point CASCADE;
CREATE TABLE saved_point
(
    saved_point_id   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT UNSIGNED NOT NULL,
    amount           INT UNSIGNED NOT NULL,
    balance          INT UNSIGNED NOT NULL,
    saved_point_type ENUM('JOIN', 'REVIEW', 'ORDER', 'CANCEL') NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at       TIMESTAMP NOT NULL
);

-- 관리자
DROP TABLE IF EXISTS admin CASCADE;
CREATE TABLE admin
(
    admin_id      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    login_id      VARCHAR(20)  NOT NULL,
    password      VARCHAR(255) NOT NULL,
    name          VARCHAR(5)   NOT NULL,
    email         VARCHAR(100) NOT NULL,
    is_authorized BOOLEAN NOT NULL,
    authority     ENUM('ROLE_ANONYMOUS', 'ROLE_ADMIN') NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    deleted_at    TIMESTAMP,
    CONSTRAINT UQ_ADMIN_LOGIN_ID UNIQUE (login_id),
    CONSTRAINT UQ_ADMIN__EMAIL UNIQUE (email)
);

-- 공지사항
DROP TABLE IF EXISTS notice CASCADE;
CREATE TABLE notice
(
    notice_id  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    admin_id   BIGINT UNSIGNED NOT NULL,
    title      VARCHAR(50) NOT NULL,
    content    TEXT        NOT NULL,
    hit        INT UNSIGNED NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 카테고리
DROP TABLE IF EXISTS category CASCADE;
CREATE TABLE category
(
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id   BIGINT NULL,
    name        VARCHAR(100) NOT NULL,
    sort_order  INT          NOT NULL DEFAULT 1,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT FK_CATEGORY_PARENT_ID FOREIGN KEY (parent_id) REFERENCES category (category_id) ON DELETE CASCADE,
    UNIQUE KEY UQ_CATEGORY_NAME (parent_id, name)
)

-- 상품
DROP TABLE IF EXISTS product CASCADE;
CREATE TABLE product
(
    product_id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    discount_id    BIGINT UNSIGNED,
    name           VARCHAR(50) NOT NULL,
    category       ENUM('STICKER', 'MASKINGTAPE', 'PHONECASE') NOT NULL,
    price          INT UNSIGNED NOT NULL,
    stock_quantity INT UNSIGNED NOT NULL,
    details        VARCHAR(1000),
    size           VARCHAR(1000),
    shipping       VARCHAR(1000),
    notice         VARCHAR(1000),
    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP,
    CONSTRAINT UQ_PRODUCT_NAME UNIQUE (name)
);

-- 상품 이미지
DROP TABLE IF EXISTS product_image CASCADE;
CREATE TABLE product_image
(
    product_image_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_id       BIGINT UNSIGNED NOT NULL,
    image_name       VARCHAR(255) NOT NULL,
    image_url        VARCHAR(500) NOT NULL,
    image_type       ENUM('DISPLAY', 'HOVER', 'DETAILS') NOT NULL
);

-- 할인
DROP TABLE IF EXISTS discount CASCADE;
CREATE TABLE discount
(
    discount_id      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(50) NOT NULL,
    discount_percent INT UNSIGNED NOT NULL,
    started_at       TIMESTAMP   NOT NULL,
    expired_at       TIMESTAMP   NOT NULL,
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP,
    CONSTRAINT UQ_DISCOUNT_NAME UNIQUE (name)
);

-- 차감 적립금
DROP TABLE IF EXISTS used_point CASCADE;
CREATE TABLE used_point
(
    used_point_id   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    payment_id      BIGINT UNSIGNED NOT NULL,
    used_point_type ENUM('ORDER', 'EXPIRE', 'CANCEL') NOT NULL,
    used_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
);

-- 차감 적립금 기록 상세
DROP TABLE IF EXISTS used_point_detail CASCADE;
CREATE TABLE used_point_detail
(
    used_point_detail_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    used_amount          INT UNSIGNED NOT NULL,
    used_point_id        BIGINT UNSIGNED NOT NULL,
    saved_point_id       BIGINT UNSIGNED NOT NULL
);

-- 위시
DROP TABLE IF EXISTS wish CASCADE;
CREATE TABLE wish
(
    wish_id    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 카트
DROP TABLE IF EXISTS cart CASCADE;
CREATE TABLE CART
(
    cart_id    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    quantity   INT UNSIGNED NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 문의
DROP TABLE IF EXISTS inquiry CASCADE;
CREATE TABLE inquiry
(
    inquiry_id  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT UNSIGNED NOT NULL,
    product_id  BIGINT UNSIGNED NOT NULL,
    answer_id   BIGINT UNSIGNED,
    title       VARCHAR(50) NOT NULL,
    content     TEXT        NOT NULL,
    is_secret   BOOLEAN NOT NULL,
    is_answered BOOLEAN NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL,
    CONSTRAINT UQ_INQUIRY_ANSWER_ID UNIQUE (answer_id)

);

-- 답변
DROP TABLE IF EXISTS answer CASCADE;
CREATE TABLE answer
(
    answer_id  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    admin_id   BIGINT UNSIGNED NOT NULL,
    content    VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 주문
DROP TABLE IF EXISTS orders CASCADE;
CREATE TABLE orders
(
    order_id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id              BIGINT UNSIGNED NOT NULL,
    payment_id             BIGINT UNSIGNED,
    merchant_uid           VARCHAR(50) NOT NULL,
    amount                 BIGINT UNSIGNED NOT NULL,
    recipient_name         VARCHAR(30),
    recipient_address      VARCHAR(255),
    recipient_phone_number VARCHAR(11),
    delivery_request_msg   VARCHAR(255),
    order_status           ENUM('TRY', 'SUCCESS', 'CANCEL', 'FAIL') NOT NULL,
    fail_reason            VARCHAR(255),
    ordered_at             TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delivered_at           TIMESTAMP,
    CONSTRAINT UQ_ORDERS_MERCHANT_UID UNIQUE (merchant_uid)
);

-- 주문x상품
DROP TABLE IF EXISTS order_product CASCADE;
CREATE TABLE order_product
(
    order_product_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    quantity         INT UNSIGNED NOT NULL,
    product_id       BIGINT UNSIGNED NOT NULL,
    order_id         BIGINT UNSIGNED NOT NULL
);

-- 결제
DROP TABLE IF EXISTS payment CASCADE;
CREATE TABLE payment
(
    payment_id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    saved_point_id BIGINT UNSIGNED NOT NULL,
    imp_uid        VARCHAR(255) NOT NULL,
    merchant_uid   VARCHAR(255) NOT NULL,
    amount         BIGINT UNSIGNED NOT NULL,
    pay_method     VARCHAR(255) NOT NULL,
    pg_provider    VARCHAR(255),
    card_name      VARCHAR(255),
    card_quota     INT UNSIGNED,
    currency       VARCHAR(10)  NOT NULL,
    buyer_email    VARCHAR(255) NOT NULL,
    payment_status VARCHAR(255),
    payment_status ENUM('PAID', 'CANCELLED', 'FAILED') NOT NULL,
    paid_at        TIMESTAMP    NOT NULL,
    failed_at      TIMESTAMP,
    cancelled_at   TIMESTAMP
);