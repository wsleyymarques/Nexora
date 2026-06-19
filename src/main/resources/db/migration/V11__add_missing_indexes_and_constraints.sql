-- V11__add_missing_indexes_and_constraints.sql

-- ISSUE #3: Add UNIQUE constraint on appointments.order_item_id (OneToOne)
ALTER TABLE appointments
    ADD CONSTRAINT uk_appointments_order_item UNIQUE (order_item_id);

-- ISSUE #8: Add missing index on OrderItem.product_id
CREATE INDEX IF NOT EXISTS idx_order_items_product
    ON order_items(product_id);

-- ISSUE #9: Add missing index on Appointments.order_item_id
CREATE INDEX IF NOT EXISTS idx_appointments_order_item
    ON appointments(order_item_id);

-- ISSUE #11: Add index on OtpCode for used status queries
CREATE INDEX IF NOT EXISTS idx_otp_code_used_expires
    ON otp_codes(used, expires_at DESC);

-- ISSUE #12: Add index on StoreMember.role for role-based queries
CREATE INDEX IF NOT EXISTS idx_store_members_role
    ON store_members(role);

-- ISSUE #13: Add index on Customer.origin
CREATE INDEX IF NOT EXISTS idx_customers_origin
    ON customers(origin);

-- ISSUE #14: Add composite index on Order for store_id + status queries
CREATE INDEX IF NOT EXISTS idx_orders_store_status
    ON orders(store_id, status);

-- ISSUE #15: Add composite index on Product for store_id + active queries
CREATE INDEX IF NOT EXISTS idx_products_store_active
    ON products(store_id, active);

-- Add index on ScheduleConfig.product_id for lookups
CREATE INDEX IF NOT EXISTS idx_schedule_configs_product
    ON schedule_configs(product_id);
