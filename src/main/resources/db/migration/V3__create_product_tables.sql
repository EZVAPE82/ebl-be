-- ============================================
-- V3: 상품 도메인 (카테고리·브랜드·상품·옵션·이미지·위시리스트)
-- ============================================

CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    parent_id   BIGINT REFERENCES categories(id),
    name        VARCHAR(80) NOT NULL,
    slug        VARCHAR(80) NOT NULL UNIQUE,
    sort_order  INT NOT NULL DEFAULT 0,
    visible     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_categories_parent ON categories(parent_id);

CREATE TABLE brands (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(80) NOT NULL UNIQUE,
    slug        VARCHAR(80) NOT NULL UNIQUE,
    logo_url    TEXT,
    sort_order  INT NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id                  BIGSERIAL PRIMARY KEY,
    category_id         BIGINT REFERENCES categories(id),
    brand_id            BIGINT REFERENCES brands(id),
    name                VARCHAR(200) NOT NULL,
    slug                VARCHAR(200) NOT NULL UNIQUE,
    description         TEXT,
    compatibility_info  TEXT,
    price               BIGINT NOT NULL,
    status              VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    thumbnail_url       TEXT,
    view_count          BIGINT NOT NULL DEFAULT 0,
    review_count        BIGINT NOT NULL DEFAULT 0,
    rating_avg          NUMERIC(3,2) NOT NULL DEFAULT 0,
    stock_threshold     INT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP,

    CONSTRAINT chk_product_status CHECK (status IN ('DRAFT','ACTIVE','SOLD_OUT','DISCONTINUED'))
);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_price ON products(price);

CREATE TABLE product_images (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url         TEXT NOT NULL,
    type        VARCHAR(16) NOT NULL,
    sort_order  INT NOT NULL DEFAULT 0,

    CONSTRAINT chk_image_type CHECK (type IN ('THUMBNAIL','DETAIL'))
);
CREATE INDEX idx_product_images_product ON product_images(product_id);

CREATE TABLE product_options (
    id              BIGSERIAL PRIMARY KEY,
    product_id      BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    option_group    VARCHAR(40) NOT NULL,
    option_value    VARCHAR(80) NOT NULL,
    price_delta     BIGINT NOT NULL DEFAULT 0,
    stock           INT NOT NULL DEFAULT 0,
    is_required     BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order      INT NOT NULL DEFAULT 0,
    visible         BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_product_options_product ON product_options(product_id);

CREATE TABLE wishlist_items (
    id          BIGSERIAL PRIMARY KEY,
    member_id   BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    product_id  BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_wishlist_member_product UNIQUE (member_id, product_id)
);
CREATE INDEX idx_wishlist_member ON wishlist_items(member_id);
