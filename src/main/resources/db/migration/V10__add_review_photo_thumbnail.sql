-- review_photos 에 thumbnail 컬럼 추가 (운영 후기 사진 업로드 지원)
--   url            : 원본 사진 URL (lightbox 풀 사이즈용)
--   thumbnail_url  : 800x800 center crop thumbnail URL (메인 그리드용)
--   thumbnail_sm   : 300x300 center crop thumbnail URL (모바일 리스트용)
--   width / height : 원본 사진 크기 (lightbox 비율 계산용)
ALTER TABLE review_photos
    ADD COLUMN thumbnail_url  TEXT,
    ADD COLUMN thumbnail_sm   TEXT,
    ADD COLUMN width          INT,
    ADD COLUMN height         INT,
    ADD COLUMN content_type   VARCHAR(40),
    ADD COLUMN file_size      BIGINT;
