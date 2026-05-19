-- ============================================
-- V6: 리뷰 · 알림 로그
-- ============================================

CREATE TABLE reviews (
    id              BIGSERIAL PRIMARY KEY,
    product_id      BIGINT NOT NULL REFERENCES products(id),
    member_id       BIGINT NOT NULL REFERENCES members(id),
    order_item_id   BIGINT NOT NULL REFERENCES order_items(id),
    rating          INT NOT NULL,
    content         TEXT,
    has_photo       BOOLEAN NOT NULL DEFAULT FALSE,
    point_rewarded  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT uq_review_order_item UNIQUE (order_item_id)
);
CREATE INDEX idx_reviews_product ON reviews(product_id, created_at DESC);
CREATE INDEX idx_reviews_member ON reviews(member_id);

CREATE TABLE review_photos (
    id          BIGSERIAL PRIMARY KEY,
    review_id   BIGINT NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    url         TEXT NOT NULL,
    sort_order  INT NOT NULL DEFAULT 0
);

CREATE TABLE notification_logs (
    id              BIGSERIAL PRIMARY KEY,
    member_id       BIGINT REFERENCES members(id),
    channel         VARCHAR(16) NOT NULL,
    template_code   VARCHAR(40) NOT NULL,
    payload         TEXT,
    status          VARCHAR(16) NOT NULL,
    failure_reason  VARCHAR(500),
    sent_at         TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_notif_channel CHECK (channel IN ('ALIMTALK','EMAIL','SMS')),
    CONSTRAINT chk_notif_status CHECK (status IN ('SENT','FAILED','SKIPPED'))
);
CREATE INDEX idx_notif_member ON notification_logs(member_id, created_at);
