-- ============================================
-- V2: 어드민 사용자 · 감사 로그
-- ============================================

CREATE TABLE admin_users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(80) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    name            VARCHAR(80) NOT NULL,
    role            VARCHAR(16) NOT NULL,
    status          VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    failed_login_count  INT NOT NULL DEFAULT 0,
    locked_until    TIMESTAMP,
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_admin_role CHECK (role IN ('MASTER','OPERATOR')),
    CONSTRAINT chk_admin_status CHECK (status IN ('ACTIVE','LOCKED','DISABLED'))
);

CREATE TABLE admin_audit_logs (
    id              BIGSERIAL PRIMARY KEY,
    admin_user_id   BIGINT NOT NULL REFERENCES admin_users(id),
    action          VARCHAR(40) NOT NULL,
    target_table    VARCHAR(40),
    target_id       BIGINT,
    before_json     TEXT,
    after_json      TEXT,
    ip_address      VARCHAR(64),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_admin_audit_admin ON admin_audit_logs(admin_user_id, created_at);
CREATE INDEX idx_admin_audit_target ON admin_audit_logs(target_table, target_id);
