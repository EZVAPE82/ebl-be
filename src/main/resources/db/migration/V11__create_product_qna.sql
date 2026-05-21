-- ============================================
-- V11: 상품 Q&A 도메인 신규 추가
-- ============================================
-- 회원이 상품에 대해 문의 작성 → 어드민/판매자가 답변.
-- isPrivate=true 인 경우 작성자 본인 + 어드민만 조회 가능 (서비스 레이어 필터링).

CREATE TABLE product_qnas (
    id              BIGSERIAL    PRIMARY KEY,
    product_id      BIGINT       NOT NULL,
    member_id       BIGINT       NOT NULL,
    question        VARCHAR(2000) NOT NULL,
    answer          VARCHAR(2000),
    answered_by     BIGINT,                          -- admin_users.id
    answered_at     TIMESTAMP,
    is_private      BOOLEAN      NOT NULL DEFAULT FALSE,
    visible         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- 공개 조회 (상품 상세에서 페이지네이션): product_id + created_at desc
CREATE INDEX idx_product_qnas_product_created ON product_qnas(product_id, created_at DESC);
-- 어드민 미답변 큐: answered_at IS NULL 빠른 필터 (H2 partial index 미지원이라
-- 일반 인덱스로. PostgreSQL 운영 환경에서도 selectivity 적당해 무해.)
CREATE INDEX idx_product_qnas_unanswered ON product_qnas(answered_at);
-- 회원 자기 Q&A 조회 (마이페이지)
CREATE INDEX idx_product_qnas_member ON product_qnas(member_id, created_at DESC);
