-- ============================================
-- V10: 성능 인덱스 보강
-- ============================================

-- 아이디(이메일) 찾기에서 phone 조회 (전체 스캔 → 인덱스)
CREATE INDEX IF NOT EXISTS idx_members_phone ON members(phone);

-- 리뷰 작성에서 order_item 직접 조회용 (현재 findAll() 우회)
CREATE INDEX IF NOT EXISTS idx_order_items_product_order ON order_items(product_id, order_id);

-- 적립금 ledger 조회 최적화 (member_id + created_at)
CREATE INDEX IF NOT EXISTS idx_point_tx_member_created ON point_transactions(member_id, created_at DESC);

-- ProductOption 동시성 — Optimistic Lock 컬럼
ALTER TABLE product_options ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
