-- ============================================
-- V1: 회원 · 인증 관련 테이블
-- 기능명세서 v1.5 / docs/erd.md 기반
-- ============================================

-- ----- members -----
CREATE TABLE members (
    id                      BIGSERIAL PRIMARY KEY,
    email                   VARCHAR(255) UNIQUE,
    password_hash           VARCHAR(255),
    name                    VARCHAR(80),
    phone                   VARCHAR(32),
    birth_date              DATE,
    gender                  VARCHAR(8),
    member_type             VARCHAR(20) NOT NULL DEFAULT 'KOREAN',
    status                  VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    join_channel            VARCHAR(16),
    referrer_member_id      BIGINT REFERENCES members(id),
    marketing_email_agreed  BOOLEAN NOT NULL DEFAULT FALSE,
    marketing_sms_agreed    BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at           TIMESTAMP,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at              TIMESTAMP,

    CONSTRAINT chk_member_type CHECK (member_type IN ('KOREAN','FOREIGN_RESIDENT','FOREIGN_OVERSEAS')),
    CONSTRAINT chk_member_status CHECK (status IN ('PENDING','ACTIVE','DORMANT','WITHDRAWN'))
);
CREATE INDEX idx_members_email ON members(email);
CREATE INDEX idx_members_status ON members(status);
CREATE INDEX idx_members_last_login_at ON members(last_login_at);

-- ----- social_accounts -----
CREATE TABLE social_accounts (
    id                  BIGSERIAL PRIMARY KEY,
    member_id           BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    provider            VARCHAR(16) NOT NULL,
    provider_user_id    VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_social_provider CHECK (provider IN ('KAKAO','GOOGLE')),
    CONSTRAINT uq_social_provider_user UNIQUE (provider, provider_user_id)
);
CREATE INDEX idx_social_accounts_member ON social_accounts(member_id);

-- ----- refresh_tokens -----
-- 보안: token 평문 저장 X, SHA-256 해시 저장
CREATE TABLE refresh_tokens (
    id              BIGSERIAL PRIMARY KEY,
    member_id       BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    user_agent      VARCHAR(255),
    ip_address      VARCHAR(64),
    expires_at      TIMESTAMP NOT NULL,
    revoked_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_refresh_tokens_member ON refresh_tokens(member_id);
CREATE INDEX idx_refresh_tokens_expires ON refresh_tokens(expires_at);

-- ----- member_terms_consents -----
CREATE TABLE member_terms_consents (
    id              BIGSERIAL PRIMARY KEY,
    member_id       BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    term_code       VARCHAR(40) NOT NULL,
    term_version    VARCHAR(16) NOT NULL,
    agreed          BOOLEAN NOT NULL,
    agreed_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_term_code CHECK (term_code IN ('TOS','PRIVACY','YOUTH','MARKETING'))
);
CREATE INDEX idx_consents_member ON member_terms_consents(member_id);

-- ----- member_adult_verifications -----
CREATE TABLE member_adult_verifications (
    id              BIGSERIAL PRIMARY KEY,
    member_id       BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    method          VARCHAR(16) NOT NULL,
    status          VARCHAR(16) NOT NULL,
    ci              VARCHAR(255),
    document_url    TEXT,
    reviewed_by     BIGINT,
    reviewed_at     TIMESTAMP,
    reject_reason   TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_av_method CHECK (method IN ('PASS','FOREIGN_DOC')),
    CONSTRAINT chk_av_status CHECK (status IN ('PENDING','APPROVED','REJECTED'))
);
CREATE INDEX idx_av_member ON member_adult_verifications(member_id);
CREATE INDEX idx_av_status ON member_adult_verifications(status);

-- ----- login_attempts -----
-- 보안 룰 #8: rate limit / brute force 방어
CREATE TABLE login_attempts (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255),
    ip_address      VARCHAR(64),
    success         BOOLEAN NOT NULL,
    user_agent      VARCHAR(255),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_login_attempts_email_time ON login_attempts(email, created_at);
CREATE INDEX idx_login_attempts_ip_time ON login_attempts(ip_address, created_at);
