-- 관리자
DROP TABLE IF EXISTS admin CASCADE;
CREATE TABLE admin
(
    admin_id      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    login_id      VARCHAR(20)  NOT NULL,
    password      VARCHAR(100) NOT NULL,
    name          VARCHAR(5)   NOT NULL,
    email         VARCHAR(45)  NOT NULL,
    is_authorized TINYINT UNSIGNED NOT NULL,
    authority     ENUM('ROLE_ANONYMOUS', 'ROLE_ADMIN') NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at   TIMESTAMP,
    CONSTRAINT ADMIN_UNIQUE_LOGIN_ID UNIQUE (login_id),
    CONSTRAINT ADMIN_UNIQUE_EMAIL UNIQUE (email)
);

-- 공지사항
DROP TABLE IF EXISTS notice CASCADE;
CREATE TABLE notice
(
    notice_id   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    admin_id    BIGINT UNSIGNED NOT NULL,
    title       VARCHAR(50)   NOT NULL,
    content     VARCHAR(2000) NOT NULL,
    hit         INT UNSIGNED NOT NULL DEFAULT 0,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP
);

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
    modified_at    TIMESTAMP,
    CONSTRAINT PRODUCT_UNIQUE_NAME UNIQUE (name)
);

-- 상품 이미지
DROP TABLE IF EXISTS product_image CASCADE;
CREATE TABLE product_image
(
    product_image_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_id       BIGINT UNSIGNED NOT NULL,
    image_name       VARCHAR(255) NOT NULL,
    image_url        VARCHAR(255) NOT NULL,
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
    CONSTRAINT DISCOUNT_UNIQUE_NAME UNIQUE (name)
);

-- 회원
DROP TABLE IF EXISTS member CASCADE;
CREATE TABLE member
(
    member_id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    membership_id         TINYINT UNSIGNED NOT NULL,
    login_id              VARCHAR(20)  NOT NULL,
    password              VARCHAR(100) NOT NULL,
    name                  VARCHAR(12)  NOT NULL,
    phone_number          VARCHAR(13)  NOT NULL,
    email                 VARCHAR(45)  NOT NULL,
    authority             ENUM('ROLE_ANONYMOUS', 'ROLE_USER', 'ROLE_ADMIN') NOT NULL,
    agree_to_receive_sms  TINYINT(1) UNSIGNED NOT NULL,
    agree_to_receive_mail TINYINT(1) UNSIGNED NOT NULL,
    is_email_verified     TINYINT(1) UNSIGNED NOT NULL,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at           TIMESTAMP,
    CONSTRAINT MEMBER_UNIQUE_LOGIN_ID UNIQUE (login_id),
    CONSTRAINT MEMBER_UNIQUE_PHONE_NUMBER UNIQUE (phone_number),
    CONSTRAINT MEMBER_UNIQUE_EMAIL UNIQUE (email)
);

-- 회원 등급
DROP TABLE IF EXISTS membership CASCADE;
CREATE TABLE membership
(
    membership_id     TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(30) NOT NULL,
    point_rate        INT UNSIGNED NOT NULL,
    level             INT UNSIGNED NOT NULL,
    max_benefit_point INT UNSIGNED,
    min_amount_spent  INT UNSIGNED NOT NULL,
    CONSTRAINT MEMBERSHIP_UNIQUE_NAME UNIQUE (name)
);

-- 배송지
DROP TABLE IF EXISTS delivery CASCADE;
CREATE TABLE delivery
(
    delivery_id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id           BIGINT UNSIGNED NOT NULL,
    delivery_name       VARCHAR(50) NOT NULL,
    recipient_name      VARCHAR(12) NOT NULL,
    zip_code            VARCHAR(5)  NOT NULL,
    basic_address       VARCHAR(40) NOT NULL,
    detail_address      VARCHAR(40),
    phone_number        VARCHAR(13) NOT NULL,
    is_default_delivery TINYINT(1) NOT NULL,
    created_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at         TIMESTAMP
);

-- 적립되는 적립금
DROP TABLE IF EXISTS saved_point CASCADE;
CREATE TABLE saved_point
(
    saved_point_id   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT UNSIGNED NOT NULL,
    amount           INT UNSIGNED NOT NULL,
    balance          INT UNSIGNED NOT NULL,
    saved_point_type ENUM('JOIN', 'REVIEW', 'ORDER') NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at       TIMESTAMP NOT NULL
);

INSERT INTO membership (name, point_rate, level, min_amount_spent)
VALUES ('basic', 1, 1, 10000)

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
    count      INT UNSIGNED NOT NULL,
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
    title       VARCHAR(50)   NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    is_secret   TINYINT(1) NOT NULL,
    is_answered TINYINT(1) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP     NOT NULL
);

-- 답변
DROP TABLE IF EXISTS answer CASCADE;
CREATE TABLE answer
(
    answer_id   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    admin_id    BIGINT UNSIGNED NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP
);

-- 주문
DROP TABLE IF EXISTS orders CASCADE;
CREATE TABLE orders
(
    order_id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id              BIGINT UNSIGNED NOT NULL,
    payment_id             BIGINT UNSIGNED,
    merchant_uid            VARCHAR(50) NOT NULL,
    amount                 BIGINT UNSIGNED NOT NULL,
    recipient_name         VARCHAR(12),
    recipient_address      VARCHAR(100),
    recipient_phone_number VARCHAR(13),
    delivery_request_msg    VARCHAR(255),
    order_status           VARCHAR(10) NOT NULL,
    fail_reason            VARCHAR(255),
    ordered_at             TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delivered_at           TIMESTAMP
);

-- 주문x상품
DROP TABLE IF EXISTS order_product CASCADE;
CREATE TABLE order_product
(
    order_product_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    count            INT NOT NULL,
    product_id       BIGINT UNSIGNED NOT NULL,
    order_id         BIGINT UNSIGNED NOT NULL
);

-- 결제
DROP TABLE IF EXISTS payment CASCADE;
CREATE TABLE payment
(
    payment_id   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    imp_uid      VARCHAR(255) NOT NULL,
    merchant_uid VARCHAR(255) NOT NULL,
    pg_provider  VARCHAR(255),
    pay_method   VARCHAR(255),
    amount       BIGINT UNSIGNED NULL,
    currency     VARCHAR(10)  NOT NULL,
    card_name    VARCHAR(255),
    card_quota  BIGINT UNSIGNED,
    buyer_email  VARCHAR(255),
    status       VARCHAR(255),
    paid_at      TIMESTAMP,
    failed_at    TIMESTAMP,
    cancelled_at TIMESTAMP
);