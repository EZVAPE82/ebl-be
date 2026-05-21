-- ===========================================================
-- local 전용 더미 시드 (디자인·기능 시각 검증용).
-- prod 적용 X — application-local.yml 에서만 로딩됨.
-- ID 는 IDENTITY 로 자동 생성됨. 단일 트랜잭션이라 1,2,3 순서 보장.
-- 이미지: placehold.co (next.config remotePatterns 허용 도메인).
-- ===========================================================

-- ===== 카테고리 4종 =====
INSERT INTO categories (parent_id, name, slug, sort_order, visible, created_at, updated_at) VALUES
(NULL, '베스트',  'best',       1, TRUE, NOW(), NOW()),
(NULL, '신상품',  'new',        2, TRUE, NOW(), NOW()),
(NULL, '일회용',  'disposable', 3, TRUE, NOW(), NOW()),
(NULL, '액상',    'liquid',     4, TRUE, NOW(), NOW());

-- ===== 브랜드 2종 =====
INSERT INTO brands (name, slug, logo_url, sort_order, created_at, updated_at) VALUES
('ELFBAR',  'elfbar',  'https://placehold.co/120x40/000/fff?text=ELFBAR',  1, NOW(), NOW()),
('ELFLIQ',  'elfliq',  'https://placehold.co/120x40/000/fff?text=ELFLIQ',  2, NOW(), NOW());

-- ===== 상품 8개 =====
-- category_id: 3=일회용, 4=액상
-- brand_id: 1=ELFBAR, 2=ELFLIQ
-- status: ON_SALE / SOLD_OUT (enum ProductStatus)
INSERT INTO products
  (category_id, brand_id, name, slug, description, compatibility_info, price, status,
   thumbnail_url, view_count, review_count, rating_avg, stock_threshold, deleted_at,
   created_at, updated_at)
VALUES
(3, 1, 'ELFBAR BC5000 그린애플',     'elfbar-bc5000-greenapple',
  '한 번 충전으로 최대 5000모금 가능한 일회용 기기. 시원한 그린애플 향.',
  '단독 사용. 충전 케이블 USB-C.',
  25000, 'ACTIVE', 'https://placehold.co/600x600/2e7d32/fff?text=BC5000+APPLE',
  342, 18, 4.7, 5, NULL, NOW(), NOW()),

(3, 1, 'ELFBAR BC5000 블루베리',     'elfbar-bc5000-blueberry',
  '5000모금 일회용. 달콤한 블루베리 노트.',
  '단독 사용. 충전 케이블 USB-C.',
  25000, 'ACTIVE', 'https://placehold.co/600x600/3949ab/fff?text=BC5000+BERRY',
  521, 32, 4.8, 5, NULL, NOW(), NOW()),

(3, 1, 'ELFBAR BC3000 망고',         'elfbar-bc3000-mango',
  '3000모금 컴팩트 일회용. 트로피컬 망고.',
  '단독 사용.',
  19000, 'ACTIVE', 'https://placehold.co/600x600/f57c00/fff?text=BC3000+MANGO',
  198, 9, 4.5, 5, NULL, NOW(), NOW()),

(3, 1, 'ELFBAR DUKE 멘솔',           'elfbar-duke-menthol',
  'DUKE 시리즈 — 묵직한 멘솔.',
  '교체형 카트리지 호환.',
  32000, 'ACTIVE', 'https://placehold.co/600x600/00897b/fff?text=DUKE+MENTHOL',
  87, 4, 4.6, 5, NULL, NOW(), NOW()),

(4, 2, 'ELFLIQ 30ml 그린애플',       'elfliq-30-greenapple',
  '리필 액상 30ml. 그린애플.',
  'POD·탱크 호환.',
  18000, 'ACTIVE', 'https://placehold.co/600x600/43a047/fff?text=ELFLIQ+APPLE',
  142, 11, 4.4, 10, NULL, NOW(), NOW()),

(4, 2, 'ELFLIQ 30ml 블루베리',       'elfliq-30-blueberry',
  '리필 액상 30ml. 블루베리.',
  'POD·탱크 호환.',
  18000, 'ACTIVE', 'https://placehold.co/600x600/5e35b1/fff?text=ELFLIQ+BERRY',
  103, 7, 4.5, 10, NULL, NOW(), NOW()),

(3, 1, 'ELFBAR 800 클래식 타바코',   'elfbar-800-tobacco',
  '800모금 입문용 일회용. 클래식 타바코 노트.',
  '단독 사용.',
  9900, 'SOLD_OUT', 'https://placehold.co/600x600/795548/fff?text=ELF800+TOBACCO',
  410, 22, 4.2, 5, NULL, NOW(), NOW()),

(4, 2, 'ELFLIQ 30ml 워터멜론',       'elfliq-30-watermelon',
  '리필 액상 30ml. 시원한 수박.',
  'POD·탱크 호환.',
  18000, 'ACTIVE', 'https://placehold.co/600x600/e53935/fff?text=ELFLIQ+MELON',
  76, 3, 4.3, 10, NULL, NOW(), NOW());

-- ===== 상품 옵션 (일부 상품에 맛/용량) =====
-- ProductOption 은 BaseTimeEntity 미상속 → created_at/updated_at 컬럼 없음
INSERT INTO product_options
  (product_id, option_group, option_value, price_delta, stock, is_required, sort_order, visible, version)
VALUES
(1, '용량', '5000puff', 0, 30, TRUE, 1, TRUE, 0),
(2, '용량', '5000puff', 0, 25, TRUE, 1, TRUE, 0),
(3, '용량', '3000puff', 0, 18, TRUE, 1, TRUE, 0),
(4, '맛',  '멘솔',     0, 12, TRUE, 1, TRUE, 0),
(4, '맛',  '쿨민트',   2000, 8, TRUE, 2, TRUE, 0),
(5, '니코틴', '0mg',   0, 40, TRUE, 1, TRUE, 0),
(5, '니코틴', '3mg',   1000, 30, TRUE, 2, TRUE, 0),
(6, '니코틴', '0mg',   0, 35, TRUE, 1, TRUE, 0),
(7, '용량', '800puff', 0, 0,  TRUE, 1, TRUE, 0),  -- 품절 옵션
(8, '니코틴', '0mg',   0, 20, TRUE, 1, TRUE, 0);

-- ===== 배너 (메인 히어로) =====
INSERT INTO banners
  (placement, image_url, link_url, alt_text, sort_order, visible, starts_at, ends_at, created_at, updated_at)
VALUES
('MAIN_HERO',
 'https://placehold.co/1920x500/1e1e1e/ffffff?text=ELFBAR+LOUNGE',
 '/c/best',
 '엘프바 라운지 — 정품 전자담배 전문몰',
 1, TRUE, NULL, NULL, NOW(), NOW());

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
 'https://placehold.co/1200x525/3949ab/fff?text=Welcome+Coupon',
 NOW(), NOW() + INTERVAL '30' DAY, TRUE, NOW(), NOW()),
('리뷰 적립금 2배 이벤트',
 '포토 리뷰 작성 시 적립금 2배 지급.',
 '구매한 상품에 사진 포함 리뷰를 남기시면 기본 적립금의 2배를 지급해드립니다.',
 'https://placehold.co/1200x525/e53935/fff?text=Photo+Review+2x',
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
