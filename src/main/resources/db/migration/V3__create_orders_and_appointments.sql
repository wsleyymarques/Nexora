-- V3__create_orders_and_appointments.sql

CREATE TABLE orders (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id    UUID            NOT NULL REFERENCES stores(id),
    customer_id UUID            NOT NULL REFERENCES customers(id),
    channel     VARCHAR(20)     NOT NULL DEFAULT 'WPP',
    status      VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    total       NUMERIC(12,2)   NOT NULL DEFAULT 0,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_store    ON orders(store_id);
CREATE INDEX idx_orders_customer ON orders(customer_id);

CREATE TABLE order_items (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id    UUID            NOT NULL REFERENCES orders(id),
    product_id  UUID            NOT NULL REFERENCES products(id),
    quantity    INT             NOT NULL DEFAULT 1,
    unit_price  NUMERIC(12,2)   NOT NULL
);

CREATE INDEX idx_order_items_order ON order_items(order_id);

CREATE TABLE appointments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_item_id   UUID        NOT NULL REFERENCES order_items(id),
    customer_id     UUID        NOT NULL REFERENCES customers(id),
    scheduled_at    TIMESTAMP   NOT NULL,
    duration_minutes INT        NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_appointments_customer   ON appointments(customer_id);
CREATE INDEX idx_appointments_scheduled  ON appointments(scheduled_at);
