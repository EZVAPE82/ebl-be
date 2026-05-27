-- ===============================================
-- banners.placement CHECK 제약에 MID_HERO 추가
-- (Banner.Placement enum 에 MID_HERO 도입 — 메인 + 중간 풀폭 hero)
-- ===============================================

-- PostgreSQL / H2 모두 ALTER ... DROP CONSTRAINT ... ADD CONSTRAINT 지원.
ALTER TABLE banners DROP CONSTRAINT chk_banner_placement;
ALTER TABLE banners ADD CONSTRAINT chk_banner_placement
    CHECK (placement IN ('MAIN_HERO', 'MID_HERO', 'TOP_STRIP', 'SECTION'));
