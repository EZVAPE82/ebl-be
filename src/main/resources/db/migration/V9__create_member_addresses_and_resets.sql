-- ============================================
-- V9: 다중 배송지 + 비밀번호 재설정 토큰
-- ============================================

CREATE TABLE member_addresses (
    id              BIGSERIAL PRIMARY KEY,
    member_id       BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    label           VARCHAR(40),
    recipient_name  VARCHAR(80) NOT NULL,
    phone           VARCHAR(32) NOT NULL,
    postal_code     VARCHAR(16) NOT NULL,
    address1        VARCHAR(200) NOT NULL,
    address2        VARCHAR(200),
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_member_addresses_member ON member_addresses(member_id);

-- 비밀번호 재설정 토큰
CREATE TABLE password_reset_tokens (
    id              BIGSERIAL PRIMARY KEY,
    member_id       BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    expires_at      TIMESTAMP NOT NULL,
    used_at         TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_pw_reset_member ON password_reset_tokens(member_id);
CREATE INDEX idx_pw_reset_expires ON password_reset_tokens(expires_at);
