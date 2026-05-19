-- ============================================
-- V4: 적립금(원장) · 쿠폰
-- ============================================

-- 적립금 원장 (ledger)
CREATE TABLE point_transactions (
    id              BIGSERIAL PRIMARY KEY,
    member_id       BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    type            VARCHAR(20) NOT NULL,
    amount          BIGINT NOT NULL,
    balance_after   BIGINT NOT NULL,
    source_type     VARCHAR(20),
    source_id       BIGINT,
    expires_at      TIMESTAMP,
    memo            VARCHAR(200),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_point_type CHECK (type IN ('EARN','USE','EXPIRE','REFUND','ADMIN_ADJUST'))
);
CREATE INDEX idx_point_tx_member ON point_transactions(member_id, created_at);
CREATE INDEX idx_point_tx_expires ON point_transactions(expires_at);
CREATE INDEX idx_point_tx_source ON point_transactions(source_type, source_id);

-- 쿠폰 정의
CREATE TABLE coupons (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(40) UNIQUE,
    name                VARCHAR(80) NOT NULL,
    type                VARCHAR(16) NOT NULL,
    discount_type       VARCHAR(16) NOT NULL,
    discount_value      BIGINT NOT NULL,
    min_order_amount    BIGINT NOT NULL DEFAULT 0,
    max_discount        BIGINT NOT NULL DEFAULT 0,
    valid_days          INT NOT NULL DEFAULT 30,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_coupon_type CHECK (type IN ('SIGNUP','BIRTHDAY','REFERRAL','MANUAL')),
    CONSTRAINT chk_discount_type CHECK (discount_type IN ('AMOUNT','PERCENT'))
);

-- 회원 보유 쿠폰
CREATE TABLE member_coupons (
    id              BIGSERIAL PRIMARY KEY,
    member_id       BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    coupon_id       BIGINT NOT NULL REFERENCES coupons(id),
    issued_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMP NOT NULL,
    used_at         TIMESTAMP,
    order_id        BIGINT
);
CREATE INDEX idx_member_coupons_member ON member_coupons(member_id);
CREATE INDEX idx_member_coupons_expires ON member_coupons(expires_at);
