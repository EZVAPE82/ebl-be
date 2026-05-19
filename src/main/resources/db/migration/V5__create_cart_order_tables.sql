-- ============================================
-- V5: 장바구니 · 주문 · 결제 · 배송 · 환불
-- ============================================

CREATE TABLE carts (
    id          BIGSERIAL PRIMARY KEY,
    member_id   BIGINT NOT NULL UNIQUE REFERENCES members(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_items (
    id                  BIGSERIAL PRIMARY KEY,
    cart_id             BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id          BIGINT NOT NULL REFERENCES products(id),
    product_option_id   BIGINT REFERENCES product_options(id),
    quantity            INT NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);

CREATE TABLE orders (
    id                  BIGSERIAL PRIMARY KEY,
    order_no            VARCHAR(32) NOT NULL UNIQUE,
    member_id           BIGINT NOT NULL REFERENCES members(id),
    status              VARCHAR(20) NOT NULL,
    total_amount        BIGINT NOT NULL,
    product_amount      BIGINT NOT NULL,
    shipping_fee        BIGINT NOT NULL DEFAULT 0,
    discount_amount     BIGINT NOT NULL DEFAULT 0,
    point_used          BIGINT NOT NULL DEFAULT 0,
    paid_amount         BIGINT NOT NULL DEFAULT 0,
    channel             VARCHAR(16) NOT NULL DEFAULT 'SELF',
    channel_order_id    VARCHAR(64),
    member_coupon_id    BIGINT,
    recipient_name      VARCHAR(80),
    recipient_phone     VARCHAR(32),
    postal_code         VARCHAR(16),
    address1            VARCHAR(200),
    address2            VARCHAR(200),
    memo                VARCHAR(200),
    ordered_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_order_status CHECK (status IN
        ('PENDING_PAYMENT','PAID','PREPARING','SHIPPING','DELIVERED','CANCELED','REFUNDED'))
);
CREATE INDEX idx_orders_member ON orders(member_id, ordered_at DESC);
CREATE INDEX idx_orders_status ON orders(status);

CREATE TABLE order_items (
    id                  BIGSERIAL PRIMARY KEY,
    order_id            BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id          BIGINT NOT NULL,
    product_option_id   BIGINT,
    product_name        VARCHAR(200) NOT NULL,
    option_text         VARCHAR(200),
    unit_price          BIGINT NOT NULL,
    quantity            INT NOT NULL,
    subtotal            BIGINT NOT NULL
);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

CREATE TABLE shipments (
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    courier     VARCHAR(40),
    tracking_no VARCHAR(64),
    shipped_at  TIMESTAMP,
    delivered_at TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 결제 거래 — 카드 번호 등 민감정보는 절대 저장 X (rule 1)
CREATE TABLE payments (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT NOT NULL REFERENCES orders(id),
    pg_provider     VARCHAR(20) NOT NULL,
    pg_tx_id        VARCHAR(64),
    method          VARCHAR(20) NOT NULL,
    amount          BIGINT NOT NULL,
    status          VARCHAR(20) NOT NULL,
    approved_at     TIMESTAMP,
    canceled_at     TIMESTAMP,
    raw_response    TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_payment_status CHECK (status IN ('READY','PAID','FAILED','CANCELED','PARTIAL_REFUND','REFUNDED'))
);
CREATE INDEX idx_payments_order ON payments(order_id);

CREATE TABLE refunds (
    id                      BIGSERIAL PRIMARY KEY,
    order_id                BIGINT NOT NULL REFERENCES orders(id),
    reason                  VARCHAR(200),
    amount                  BIGINT NOT NULL,
    shipping_fee_deducted   BIGINT NOT NULL DEFAULT 0,
    status                  VARCHAR(20) NOT NULL,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at            TIMESTAMP,

    CONSTRAINT chk_refund_status CHECK (status IN ('REQUESTED','APPROVED','REJECTED','COMPLETED'))
);
