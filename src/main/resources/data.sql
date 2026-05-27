-- ===========================================================
-- local 전용 더미 시드 (디자인·기능 시각 검증용).
-- prod 적용 X — application-local.yml 에서만 로딩됨.
-- ID 는 IDENTITY 로 자동 생성됨. 단일 트랜잭션이라 1,2,3 순서 보장.
-- 이미지: placehold.co (next.config remotePatterns 허용 도메인).
-- ===========================================================

-- ===== 카테고리 7종 (Header NAV 와 매핑) =====
INSERT INTO categories (parent_id, name, slug, sort_order, visible, created_at, updated_at) VALUES
(NULL, '베스트',       'best',       1, TRUE, NOW(), NOW()),
(NULL, '신상품',       'new',        2, TRUE, NOW(), NOW()),
(NULL, '일회용',       'disposable', 3, TRUE, NOW(), NOW()),
(NULL, '액상',         'liquid',     4, TRUE, NOW(), NOW()),
(NULL, '기기',         'devices',    5, TRUE, NOW(), NOW()),
(NULL, '맛·카트리지',  'cartridge',  6, TRUE, NOW(), NOW()),
(NULL, '악세사리',     'accessory',  7, TRUE, NOW(), NOW());

-- ===== 브랜드 2종 =====
INSERT INTO brands (name, slug, logo_url, sort_order, created_at, updated_at) VALUES
('ELFBAR',  'elfbar',  'https://placehold.co/120x40/000/fff?text=ELFBAR',  1, NOW(), NOW()),
('ELFLIQ',  'elfliq',  'https://placehold.co/120x40/000/fff?text=ELFLIQ',  2, NOW(), NOW());

-- ===== 상품 24개 =====
-- category_id: 3=일회용, 4=액상, 5=기기, 6=카트리지, 7=악세사리
-- brand_id: 1=ELFBAR, 2=ELFLIQ
-- status: ACTIVE / SOLD_OUT
INSERT INTO products
  (category_id, brand_id, name, slug, description, compatibility_info, price, status,
   thumbnail_url, view_count, review_count, rating_avg, stock_threshold, deleted_at,
   created_at, updated_at)
VALUES
-- ===== 일회용 (8개) — 시안 11:864 추천 아이템 4종 (Apple Ice·Blue Razz·Cola Ice·Grape Cherry) 매핑 =====
(3, 1, 'ELFBAR Apple Ice',           'elfbar-apple-ice',
  '5000모금 일회용. 시원한 청사과 + 멘솔. 시안 색상: 핑크 그라데이션.',
  '단독 사용. 충전 케이블 USB-C.',
  25000, 'ACTIVE', '/images/prod-apple-ice.png',
  342, 18, 4.7, 5, NULL, NOW(), NOW()),
(3, 1, 'ELFBAR Blue Razz Ice',       'elfbar-blue-razz-ice',
  '5000모금 일회용. 블루 라즈베리 + 멘솔. 시안 색상: 블랙 그라데이션.',
  '단독 사용. 충전 케이블 USB-C.',
  25000, 'ACTIVE', '/images/prod-blue-razz.png',
  521, 32, 4.8, 5, NULL, NOW(), NOW()),
(3, 1, 'ELFBAR Cola Ice',            'elfbar-cola-ice',
  '5000모금 일회용. 콜라 + 멘솔. 시안 색상: 그린 그라데이션.',
  '단독 사용. USB-C 고속 충전.',
  25000, 'ACTIVE', '/images/prod-cola-ice.png',
  198, 9, 4.5, 5, NULL, NOW(), NOW()),
(3, 1, 'ELFBAR Grape Cherry',        'elfbar-grape-cherry',
  '5000모금 일회용. 포도 + 체리. 시안 색상: 핑크 그라데이션.',
  '단독 사용. USB-C 고속 충전.',
  25000, 'ACTIVE', '/images/prod-grape-cherry.png',
  892, 67, 4.9, 5, NULL, NOW(), NOW()),
(3, 1, 'ELFBAR DUKE 멘솔',           'elfbar-duke-menthol',
  'DUKE 시그니처 — 묵직한 멘솔.',
  '교체형 카트리지 호환.',
  32000, 'ACTIVE', '/images/elfbar-product-2.png',
  287, 14, 4.6, 5, NULL, NOW(), NOW()),
(3, 1, 'ELFBAR DUKE 블루베리아이스', 'elfbar-duke-bbi',
  'DUKE 시그니처 — 블루베리 아이스. 청량+달콤.',
  '교체형 카트리지 호환.',
  32000, 'ACTIVE', '/images/elfbar-product-1.png',
  165, 8, 4.7, 5, NULL, NOW(), NOW()),
(3, 1, 'ELFBAR DUKE 피치',           'elfbar-duke-peach',
  'DUKE 시그니처 — 부드러운 피치.',
  '교체형 카트리지 호환.',
  32000, 'ACTIVE', '/images/elfbar-product-2.png',
  124, 5, 4.5, 5, NULL, NOW(), NOW()),
(3, 1, 'ELFBAR 800 클래식 타바코',   'elfbar-800-tobacco',
  '800모금 입문용 일회용. 클래식 타바코 노트.',
  '단독 사용.',
  9900, 'SOLD_OUT', '/images/elfbar-product-1.png',
  410, 22, 4.2, 5, NULL, NOW(), NOW()),

-- ===== 액상 (8개) =====
(4, 2, 'ELFLIQ 30ml 그린애플',       'elfliq-30-greenapple',
  '리필 액상 30ml. 그린애플.',
  'POD·탱크 호환.',
  18000, 'ACTIVE', '/images/elfbar-product-1.png',
  142, 11, 4.4, 10, NULL, NOW(), NOW()),
(4, 2, 'ELFLIQ 30ml 블루베리',       'elfliq-30-blueberry',
  '리필 액상 30ml. 블루베리.',
  'POD·탱크 호환.',
  18000, 'ACTIVE', '/images/elfbar-product-2.png',
  103, 7, 4.5, 10, NULL, NOW(), NOW()),
(4, 2, 'ELFLIQ 30ml 워터멜론',       'elfliq-30-watermelon',
  '리필 액상 30ml. 시원한 수박.',
  'POD·탱크 호환.',
  18000, 'ACTIVE', '/images/elfbar-product-2.png',
  76, 3, 4.3, 10, NULL, NOW(), NOW()),
(4, 2, 'ELFLIQ 30ml 망고탱고',       'elfliq-30-mango',
  '리필 액상 30ml. 망고 + 스트로베리.',
  'POD·탱크 호환.',
  18000, 'ACTIVE', '/images/elfbar-product-1.png',
  88, 6, 4.5, 10, NULL, NOW(), NOW()),
(4, 2, 'ELFLIQ 30ml 멘솔아이스',     'elfliq-30-menthol',
  '리필 액상 30ml. 강한 멘솔.',
  'POD·탱크 호환.',
  18000, 'ACTIVE', '/images/elfbar-product-2.png',
  152, 13, 4.6, 10, NULL, NOW(), NOW()),
(4, 2, 'ELFLIQ 60ml 그레이프 패션',  'elfliq-60-grape',
  '대용량 리필 액상 60ml. 그레이프 + 패션후르츠.',
  'POD·탱크 호환.',
  29000, 'ACTIVE', '/images/elfbar-product-1.png',
  61, 4, 4.4, 10, NULL, NOW(), NOW()),
(4, 2, 'ELFLIQ 60ml 트리플베리',     'elfliq-60-berry',
  '대용량 리필 액상 60ml. 블루베리·라즈베리·블랙베리.',
  'POD·탱크 호환.',
  29000, 'ACTIVE', '/images/elfbar-product-2.png',
  47, 2, 4.5, 10, NULL, NOW(), NOW()),
(4, 2, 'ELFLIQ 30ml 타바코',          'elfliq-30-tobacco',
  '리필 액상 30ml. 클래식 타바코.',
  'POD·탱크 호환.',
  18000, 'ACTIVE', '/images/elfbar-product-1.png',
  38, 1, 4.0, 10, NULL, NOW(), NOW()),

-- ===== 기기 (4개) =====
(5, 1, 'ELFBAR Mate500 충전기기',    'elfbar-mate500',
  '리필 가능한 POD 기기. 500mAh 배터리.',
  'ELFLIQ 카트리지 호환.',
  35000, 'ACTIVE', '/images/elfbar-product-1.png',
  178, 12, 4.6, 5, NULL, NOW(), NOW()),
(5, 1, 'ELFBAR PI7000 프로',          'elfbar-pi7000',
  'POD 시스템 + 가변 출력. 7000 puff.',
  'ELFLIQ POD 전용.',
  55000, 'ACTIVE', '/images/elfbar-product-2.png',
  92, 6, 4.7, 5, NULL, NOW(), NOW()),
(5, 1, 'ELFBAR Lowit 5000',           'elfbar-lowit-5000',
  '교체형 카트리지 시스템. 0%~5% 니코틴 선택.',
  '전용 Lowit 카트리지.',
  45000, 'ACTIVE', '/images/elfbar-product-1.png',
  113, 9, 4.5, 5, NULL, NOW(), NOW()),
(5, 1, 'ELFBAR Voopoo Argus',         'elfbar-argus',
  '엔트리 POD 기기. 슬림 디자인.',
  '범용 POD 호환.',
  29000, 'ACTIVE', '/images/elfbar-product-2.png',
  54, 2, 4.3, 5, NULL, NOW(), NOW()),

-- ===== 맛·카트리지 (3개) =====
(6, 1, 'ELFBAR Lowit POD 멘솔',       'elfbar-lowit-pod-menthol',
  'Lowit 전용 교체 카트리지. 2ml. 멘솔.',
  'ELFBAR Lowit 5000 전용.',
  12000, 'ACTIVE', '/images/elfbar-product-1.png',
  201, 18, 4.6, 20, NULL, NOW(), NOW()),
(6, 1, 'ELFBAR Lowit POD 그린애플',  'elfbar-lowit-pod-greenapple',
  'Lowit 전용 교체 카트리지. 2ml. 그린애플.',
  'ELFBAR Lowit 5000 전용.',
  12000, 'ACTIVE', '/images/elfbar-product-2.png',
  167, 13, 4.5, 20, NULL, NOW(), NOW()),
(6, 1, 'ELFBAR Lowit POD 블루베리',  'elfbar-lowit-pod-blueberry',
  'Lowit 전용 교체 카트리지. 2ml. 블루베리.',
  'ELFBAR Lowit 5000 전용.',
  12000, 'ACTIVE', '/images/elfbar-product-1.png',
  142, 9, 4.5, 20, NULL, NOW(), NOW()),

-- ===== 악세사리 (3개) =====
(7, 1, 'ELFBAR USB-C 충전 케이블',    'elfbar-cable-usbc',
  '정품 USB-C 충전 케이블 1m. 빠른 충전 지원.',
  '모든 ELFBAR 기기.',
  4000, 'ACTIVE', '/images/elfbar-product-1.png',
  324, 28, 4.5, 30, NULL, NOW(), NOW()),
(7, 1, 'ELFBAR 실리콘 케이스',         'elfbar-case',
  '제품 보호용 실리콘 케이스. 다양한 컬러.',
  'BC5000 시리즈.',
  6000, 'ACTIVE', '/images/elfbar-product-2.png',
  98, 5, 4.4, 20, NULL, NOW(), NOW()),
(7, 1, 'ELFBAR 휴대용 거치대',         'elfbar-stand',
  '책상용 슬림 거치대. 마그네틱 부착.',
  '모든 ELFBAR 기기.',
  8000, 'ACTIVE', '/images/elfbar-product-1.png',
  41, 2, 4.3, 15, NULL, NOW(), NOW());

-- ===== 상품 옵션 — product_id 는 위 INSERT 순서 (1~26) 와 매칭 =====
-- ProductOption 은 BaseTimeEntity 미상속 → created_at/updated_at 컬럼 없음
INSERT INTO product_options
  (product_id, option_group, option_value, price_delta, stock, is_required, sort_order, visible, version)
VALUES
-- 일회용 (1~8) — 시안 4 컬러: Apple Ice / Blue Razz Ice / Cola Ice / Grape Cherry
(1, '니코틴', '0mg',      0,    30, TRUE, 1, TRUE, 0),
(1, '니코틴', '3mg',      1000, 20, TRUE, 2, TRUE, 0),
(2, '니코틴', '0mg',      0,    25, TRUE, 1, TRUE, 0),
(2, '니코틴', '3mg',      1000, 18, TRUE, 2, TRUE, 0),
(3, '니코틴', '0mg',      0,    22, TRUE, 1, TRUE, 0),
(4, '니코틴', '0mg',      0,    20, TRUE, 1, TRUE, 0),
(4, '니코틴', '3mg',      1000, 15, TRUE, 2, TRUE, 0),
(5, '맛',     '멘솔',     0, 12, TRUE, 1, TRUE, 0),
(5, '맛',     '쿨민트',   2000, 8, TRUE, 2, TRUE, 0),
(6, '맛',     '블루베리아이스', 0, 10, TRUE, 1, TRUE, 0),
(7, '맛',     '피치',     0, 14, TRUE, 1, TRUE, 0),
(8, '용량',   '800puff', 0, 0,  TRUE, 1, TRUE, 0),    -- 품절 옵션
-- 액상 (9~16)
(9,  '니코틴', '0mg',     0, 40, TRUE, 1, TRUE, 0),
(9,  '니코틴', '3mg',     1000, 30, TRUE, 2, TRUE, 0),
(10, '니코틴', '0mg',     0, 35, TRUE, 1, TRUE, 0),
(10, '니코틴', '3mg',     1000, 25, TRUE, 2, TRUE, 0),
(11, '니코틴', '0mg',     0, 20, TRUE, 1, TRUE, 0),
(12, '니코틴', '0mg',     0, 28, TRUE, 1, TRUE, 0),
(13, '니코틴', '3mg',     0, 22, TRUE, 1, TRUE, 0),
(14, '니코틴', '0mg',     0, 18, TRUE, 1, TRUE, 0),
(15, '니코틴', '0mg',     0, 16, TRUE, 1, TRUE, 0),
(16, '니코틴', '3mg',     0, 12, TRUE, 1, TRUE, 0),
-- 기기 (17~20) — 색상 옵션
(17, '색상', '블랙',      0, 15, TRUE, 1, TRUE, 0),
(17, '색상', '실버',      0, 12, TRUE, 2, TRUE, 0),
(18, '색상', '블랙',      0, 8,  TRUE, 1, TRUE, 0),
(18, '색상', '레드',      0, 6,  TRUE, 2, TRUE, 0),
(19, '색상', '블랙',      0, 10, TRUE, 1, TRUE, 0),
(20, '색상', '실버',      0, 14, TRUE, 1, TRUE, 0),
-- 카트리지 (21~23) — 니코틴 옵션
(21, '니코틴', '0mg',     0, 25, TRUE, 1, TRUE, 0),
(21, '니코틴', '3mg',     0, 20, TRUE, 2, TRUE, 0),
(22, '니코틴', '0mg',     0, 22, TRUE, 1, TRUE, 0),
(23, '니코틴', '0mg',     0, 18, TRUE, 1, TRUE, 0),
-- 악세사리 (24~26) — 옵션 없음 (단일 상품)
(25, '색상', '블랙',      0, 8,  TRUE, 1, TRUE, 0),
(25, '색상', '화이트',    0, 6,  TRUE, 2, TRUE, 0),
(25, '색상', '핑크',      0, 4,  TRUE, 3, TRUE, 0);

-- ===== 배너 (메인 히어로 3장 + 중간 히어로 3장) =====
-- 시안 2026-05-26 업데이트: Figma 노드 41:8762/41:8761/41:8763 → hero/hero-2/hero-3.png (1920x800 hero 비율)
INSERT INTO banners
  (placement, image_url, link_url, alt_text, sort_order, visible, starts_at, ends_at, created_at, updated_at)
VALUES
('MAIN_HERO', '/images/hero.png',   '/c/best',       '엘프바 BC10000 — NEW ARRIVAL', 1, TRUE, NULL, NULL, NOW(), NOW()),
('MAIN_HERO', '/images/hero-2.png', '/c/disposable', '엘프바 시그니처 라인업',          2, TRUE, NULL, NULL, NOW(), NOW()),
('MAIN_HERO', '/images/hero-3.png', '/c/disposable', '엘프바 프리미엄 컬렉션',          3, TRUE, NULL, NULL, NOW(), NOW()),

-- 중간 히어로 1장 (단일 cinematic banner; duke-banner/duke-full-banner는 3패널 mockup이라 부적합)
('MID_HERO',  '/images/hero-3.png', '/c/disposable', 'ELFBAR DUKE 시그니처', 1, TRUE, NULL, NULL, NOW(), NOW());

-- ===== 공지사항 3건 =====
INSERT INTO notices (title, content, pinned, visible, view_count, created_at, updated_at) VALUES
('[필독] 만 19세 이상 성인 인증 안내',
 '본 사이트는 청소년보호법에 따라 만 19세 이상 성인만 이용 가능합니다. 가입 후 PASS 본인인증을 완료해야 결제가 가능합니다.',
 TRUE, TRUE, 1320, NOW(), NOW()),
('5월 정기 배송 점검 안내',
 '5월 25일(일) 02:00~05:00 사이 배송 시스템 점검이 진행됩니다. 해당 시간대 주문은 다음 영업일에 출고됩니다.',
 FALSE, TRUE, 84, NOW(), NOW()),
('이벤트 적립금 지급 일정 안내',
 '리뷰 작성 적립금은 배송 완료 후 7일 이내 자동 지급됩니다. 포토 리뷰는 적립금 2배.',
 FALSE, TRUE, 156, NOW(), NOW());

-- ===== FAQ 5건 (카테고리 2종) =====
INSERT INTO faqs (category, question, answer, sort_order, visible, created_at, updated_at) VALUES
('주문·결제', '결제 수단은 어떤 것이 있나요?',
 '신용/체크카드, 카카오페이, 네이버페이, 토스페이, 가상계좌를 지원합니다.', 1, TRUE, NOW(), NOW()),
('주문·결제', '주문 후 취소는 언제까지 가능한가요?',
 '결제 완료 후 상품 준비 단계 이전까지 마이페이지에서 즉시 취소 가능합니다.', 2, TRUE, NOW(), NOW()),
('배송', '배송 기간은 얼마나 걸리나요?',
 '평일 기준 1~3일 소요됩니다. 일정 금액 이상 주문 시 무료배송이 적용됩니다.', 1, TRUE, NOW(), NOW()),
('배송', '도서·산간 지역도 배송 가능한가요?',
 '제주 및 일부 도서·산간 지역은 추가 배송비 3,000원이 부과됩니다.', 2, TRUE, NOW(), NOW()),
('회원·인증', '만 19세 미만도 이용 가능한가요?',
 '아니요. 본 사이트는 청소년보호법에 따라 만 19세 이상만 이용 가능합니다.', 1, TRUE, NOW(), NOW());

-- ===== 이벤트 2건 =====
INSERT INTO events (title, summary, content, banner_url, starts_at, ends_at, visible, created_at, updated_at) VALUES
('신규 회원 5천원 쿠폰',
 '가입 즉시 5,000원 쿠폰이 자동 발급됩니다.',
 '이벤트 기간 중 가입한 모든 회원에게 5,000원 즉시 사용 쿠폰을 지급합니다. 최소 주문금액 30,000원 이상.',
 'https://images.unsplash.com/photo-1516383074327-ac4841225abf?w=1200&q=80',
 NOW(), NOW() + INTERVAL '30' DAY, TRUE, NOW(), NOW()),
('리뷰 적립금 2배 이벤트',
 '포토 리뷰 작성 시 적립금 2배 지급.',
 '구매한 상품에 사진 포함 리뷰를 남기시면 기본 적립금의 2배를 지급해드립니다.',
 'https://images.unsplash.com/photo-1531297484001-80022131f5a1?w=1200&q=80',
 NOW(), NOW() + INTERVAL '60' DAY, TRUE, NOW(), NOW());

-- ===== 테스트 회원 1명 =====
-- email: test@test.com / password: Test1234!@  (BCrypt-12)
-- status=ACTIVE (성인인증 완료 가정), member_type=KOREAN
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
 NOW(), NOW());

-- ===== 테스트 어드민 1명 =====
-- username: admin / password: Admin1234!@  (BCrypt-12)
-- role=MASTER, status=ACTIVE
INSERT INTO admin_users
  (username, password_hash, name, role, status, failed_login_count, locked_until, last_login_at,
   created_at, updated_at)
VALUES
('admin',
 '$2b$12$mcaw879CEiSMYi5oGL9x7u7tTzvn15yI/ChmaIvuC8vWUcPtMiJW.',
 '시스템 운영자', 'MASTER', 'ACTIVE', 0, NULL, NULL,
 NOW(), NOW());

-- ===== 프로모션 2건 (BOGO_SAME) =====
-- promotion 1: ELFBAR BC5000 시리즈 (product_id 1·2) 2+1 30일
-- promotion 2: ELFLIQ 액상 (product_id 9·10·11·12·13) 10+1 60일
INSERT INTO promotions
  (name, type, buy_quantity, get_quantity, gift_product_id, gift_product_option_id,
   valid_from, valid_to, active, created_at, updated_at)
VALUES
('BC5000 시리즈 2+1', 'BOGO_SAME', 2, 1, NULL, NULL,
 NOW() - INTERVAL '1' DAY, NOW() + INTERVAL '30' DAY, TRUE, NOW(), NOW()),
('ELFLIQ 30ml 액상 10+1', 'BOGO_SAME', 10, 1, NULL, NULL,
 NOW() - INTERVAL '1' DAY, NOW() + INTERVAL '60' DAY, TRUE, NOW(), NOW());

-- 프로모션 적용 상품 매핑
INSERT INTO promotion_products (promotion_id, product_id) VALUES
(1, 1), (1, 2),
(2, 9), (2, 10), (2, 11), (2, 12), (2, 13);

-- ===== 상품 Q&A 시드 4건 (members + admin INSERT 이후) =====
-- member_id=1 (test@test.com), answered_by=1 (admin)
INSERT INTO product_qnas
  (product_id, member_id, question, answer, answered_by, answered_at,
   is_private, visible, created_at, updated_at)
VALUES
(1, 1,
 '배송은 얼마나 걸리나요? 평일 오후에 주문하면 다음날 받을 수 있을까요?',
 '평일 13시 이전 주문 시 당일 출고됩니다. 다음날 도착 가능합니다 :)',
 1, NOW() - INTERVAL '2' DAY, FALSE, TRUE, NOW() - INTERVAL '3' DAY, NOW() - INTERVAL '2' DAY),
(1, 1,
 '맛은 어떤가요? 너무 달지 않았으면 좋겠어요.',
 '그린애플 특유의 청량감과 약간의 단맛. 시원한 후미가 인상적입니다.',
 1, NOW() - INTERVAL '1' DAY, FALSE, TRUE, NOW() - INTERVAL '1' DAY, NOW() - INTERVAL '1' DAY),
(2, 1,
 '입고 일정 문의드립니다.',
 NULL, NULL, NULL, FALSE, TRUE, NOW() - INTERVAL '6' HOUR, NOW() - INTERVAL '6' HOUR),
(2, 1,
 '교환 가능한지 비공개로 문의드립니다.',
 NULL, NULL, NULL, TRUE, TRUE, NOW() - INTERVAL '2' HOUR, NOW() - INTERVAL '2' HOUR);
