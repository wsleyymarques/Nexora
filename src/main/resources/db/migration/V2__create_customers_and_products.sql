-- V2__create_customers_and_products.sql

CREATE TABLE customers (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id    UUID                NOT NULL REFERENCES stores(id),
    name        VARCHAR(150)        NOT NULL,
    phone       VARCHAR(30),
    email       VARCHAR(255),
    origin      VARCHAR(20)         NOT NULL DEFAULT 'WPP',
    created_at  TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_customers_store ON customers(store_id);

CREATE TABLE products (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id    UUID                NOT NULL REFERENCES stores(id),
    name        VARCHAR(200)        NOT NULL,
    description TEXT,
    price       NUMERIC(12,2)       NOT NULL,
    type        VARCHAR(20)         NOT NULL DEFAULT 'PHYSICAL',
    active      BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_products_store ON products(store_id);

CREATE TABLE schedule_configs (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id       UUID        NOT NULL UNIQUE REFERENCES products(id),
    duration_minutes INT         NOT NULL,
    available_days   VARCHAR(20) NOT NULL, -- ex: "MON,TUE,WED,THU,FRI"
    start_time       TIME        NOT NULL,
    end_time         TIME        NOT NULL
);
