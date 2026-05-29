-- 테스트 계정 보장 — 사용자 확인용 (시안 페이지 5종 검수)
-- email: test@test.com
-- password: Test1234!@  (BCrypt-12)
-- 시드는 INSERT 만 하므로 기존 row 있을 시 비밀번호 못 바꿈. UPSERT 로 강제 동기화.

INSERT INTO members
  (email, password_hash, name, phone, birth_date, gender, member_type, status, join_channel,
   referrer_member_id, marketing_email_agreed, marketing_sms_agreed, last_login_at, deleted_at,
   created_at, updated_at)
VALUES
('test@test.com',
 '$2b$12$BpR3UQepu9/FcEa4V9/nG.Xhl2FkACr.AIJr1rbOsar0zot0N5CMm',
 '엘프바테스터', '010-1234-5678', '1995-06-15', 'M',
 'KOREAN', 'ACTIVE', 'PC',
 NULL, TRUE, TRUE, NULL, NULL,
 NOW(), NOW())
ON CONFLICT (email) DO UPDATE
SET password_hash = EXCLUDED.password_hash,
    status        = 'ACTIVE',
    deleted_at    = NULL,
    updated_at    = NOW();
