-- ============================================
-- V7: 콘텐츠 (공지·FAQ·이벤트·팝업·배너)
-- ============================================

CREATE TABLE notices (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    content     TEXT NOT NULL,
    pinned      BOOLEAN NOT NULL DEFAULT FALSE,
    visible     BOOLEAN NOT NULL DEFAULT TRUE,
    view_count  BIGINT NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_notices_visible_pinned ON notices(visible, pinned, created_at DESC);

CREATE TABLE faqs (
    id          BIGSERIAL PRIMARY KEY,
    category    VARCHAR(40),
    question    VARCHAR(300) NOT NULL,
    answer      TEXT NOT NULL,
    sort_order  INT NOT NULL DEFAULT 0,
    visible     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_faqs_category ON faqs(category, sort_order);

CREATE TABLE events (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    summary     VARCHAR(500),
    content     TEXT,
    banner_url  TEXT,
    starts_at   TIMESTAMP,
    ends_at     TIMESTAMP,
    visible     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_events_period ON events(visible, starts_at, ends_at);

CREATE TABLE banners (
    id          BIGSERIAL PRIMARY KEY,
    placement   VARCHAR(20) NOT NULL,
    image_url   TEXT NOT NULL,
    link_url    TEXT,
    alt_text    VARCHAR(200),
    sort_order  INT NOT NULL DEFAULT 0,
    visible     BOOLEAN NOT NULL DEFAULT TRUE,
    starts_at   TIMESTAMP,
    ends_at     TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_banner_placement CHECK (placement IN ('MAIN_HERO','TOP_STRIP','SECTION'))
);
CREATE INDEX idx_banners_placement_visible ON banners(placement, visible, sort_order);

CREATE TABLE popups (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    image_url       TEXT,
    link_url        TEXT,
    content_html    TEXT,
    sort_order      INT NOT NULL DEFAULT 0,
    visible         BOOLEAN NOT NULL DEFAULT TRUE,
    starts_at       TIMESTAMP,
    ends_at         TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_popups_visible ON popups(visible, sort_order);
