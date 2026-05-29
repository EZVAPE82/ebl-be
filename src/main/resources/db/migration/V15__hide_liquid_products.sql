-- 액상 (e-liquid) 판매 중단 — 카테고리/상품/프로모션 비활성화
-- 비즈니스 결정: 액상 카테고리를 더 이상 판매하지 않음 (2026-05-29).
-- 데이터 보존을 위해 DELETE 가 아닌 visible/status/active 플래그 toggle.

-- 카테고리 숨김
UPDATE categories
   SET visible = FALSE, updated_at = CURRENT_TIMESTAMP
 WHERE slug = 'liquid';

-- 액상 카테고리 소속 상품 단종 처리 (status = DISCONTINUED)
UPDATE products
   SET status = 'DISCONTINUED', updated_at = CURRENT_TIMESTAMP
 WHERE category_id IN (SELECT id FROM categories WHERE slug = 'liquid');

-- 액상 관련 프로모션 비활성화 (ELFLIQ 액상 10+1 등)
UPDATE promotions
   SET active = FALSE, updated_at = CURRENT_TIMESTAMP
 WHERE name LIKE '%액상%' OR name LIKE '%ELFLIQ%';
