-- ============================================
-- V8: 정책 설정값 (key-value)
-- 컬럼명 setting_value (H2/PG에서 value는 reserved)
-- ============================================

CREATE TABLE policy_settings (
    setting_key     VARCHAR(60) PRIMARY KEY,
    setting_value   VARCHAR(200) NOT NULL,
    description     VARCHAR(500),
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO policy_settings (setting_key, setting_value, description) VALUES
    ('free_shipping_threshold', '50000', '무료배송 기준 금액 (원)'),
    ('point_min_use_amount',    '1000',  '적립금 최소 사용 금액 (원)'),
    ('point_min_balance',       '1000',  '적립금 사용 위한 최소 보유 금액 (원)'),
    ('point_max_use_rate',      '50',    '결제금액 대비 적립금 최대 사용 비율 (%)'),
    ('return_shipping_fee',     '3000',  '환불 시 자동 차감 반송 택배비 (원)'),
    ('coupon_point_concurrent', 'true',  '쿠폰·적립금 중복 사용 허용 여부 (true/false)'),
    ('shipping_fee_default',    '3000',  '기본 배송비 (원, 무료배송 미만일 때)');
