-- ============================================
-- V12: 프로모션 (2+1·10+1 등 BOGO) 도메인
-- ============================================
-- 정책 (docs/promotions-feasibility.md):
--   - 쿠폰 중복: 허용 (paid >= 0 보장, 캠페인별 토글은 추후 컬럼 추가 가능)
--   - 무료 라인 적립금: 제외 (PromotionEvaluator 가 PAID 라인 기준만 적립 계산)
--   - 부분 환불 시 사은품: 유료 환불 비율로 비례 회수 (RefundService 통합)
--   - 증정: 동일 상품 기본 (gift_product_id NULL), 별도 사은품도 스키마 지원

CREATE TABLE promotions (
    id              BIGSERIAL    PRIMARY KEY,
    name            VARCHAR(80)  NOT NULL,
    type            VARCHAR(16)  NOT NULL,            -- BOGO_SAME, BOGO_OTHER
    buy_quantity    INT          NOT NULL,            -- 2 (2+1 의 2)
    get_quantity    INT          NOT NULL,            -- 1 (2+1 의 1)
    gift_product_id          BIGINT,                  -- NULL 이면 트리거 상품과 동일
    gift_product_option_id   BIGINT,
    valid_from      TIMESTAMP    NOT NULL,
    valid_to        TIMESTAMP    NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE promotion_products (
    promotion_id    BIGINT       NOT NULL REFERENCES promotions(id) ON DELETE CASCADE,
    product_id      BIGINT       NOT NULL,
    PRIMARY KEY (promotion_id, product_id)
);

-- 활성·유효기간 빠른 조회 (PromotionEvaluator 가 매 결제마다 호출)
CREATE INDEX idx_promotions_active_period ON promotions(active, valid_from, valid_to);

-- order_items 에 사은품 라인 표시용 컬럼 추가 (H2 다중 ADD COLUMN 미지원 → 분리)
ALTER TABLE order_items ADD COLUMN kind VARCHAR(16) NOT NULL DEFAULT 'PAID';
ALTER TABLE order_items ADD COLUMN source_promotion_id BIGINT;

CREATE INDEX idx_order_items_promotion ON order_items(source_promotion_id);
