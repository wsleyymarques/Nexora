-- V6__sync_domain_schema.sql

-- Schedule configs now use the current resource-based model.
ALTER TABLE schedule_configs
    ADD COLUMN IF NOT EXISTS resource_type VARCHAR(20) NOT NULL DEFAULT 'PERSON';

ALTER TABLE schedule_configs
    DROP COLUMN IF EXISTS available_days;

ALTER TABLE schedule_configs
    DROP COLUMN IF EXISTS start_time;

ALTER TABLE schedule_configs
    DROP COLUMN IF EXISTS end_time;

-- Order items now extend BaseEntity and store optional estimated duration.
ALTER TABLE order_items
    ADD COLUMN IF NOT EXISTS estimated_minutes INT;

ALTER TABLE order_items
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();

ALTER TABLE order_items
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();

-- Resources are required by availability and appointment scheduling.
CREATE TABLE IF NOT EXISTS resources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id UUID NOT NULL REFERENCES stores(id),
    store_member_id UUID REFERENCES store_members(id),
    name VARCHAR(150) NOT NULL,
    type VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_resources_store
    ON resources(store_id);

CREATE INDEX IF NOT EXISTS idx_resources_store_type_active
    ON resources(store_id, type, active);

CREATE INDEX IF NOT EXISTS idx_resources_member
    ON resources(store_member_id);

-- Seed one default resource per store so appointments can be backfilled.
INSERT INTO resources (store_id, name, type, active)
SELECT s.id, 'Default resource', 'PERSON', TRUE
FROM stores s
WHERE NOT EXISTS (
    SELECT 1
    FROM resources r
    WHERE r.store_id = s.id
      AND r.name = 'Default resource'
      AND r.type = 'PERSON'
);

CREATE TABLE IF NOT EXISTS resource_availabilities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resource_id UUID NOT NULL REFERENCES resources(id),
    day_of_week VARCHAR(10) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (resource_id, day_of_week)
);

CREATE INDEX IF NOT EXISTS idx_resource_availabilities_resource_day_active
    ON resource_availabilities(resource_id, day_of_week, active);

CREATE TABLE IF NOT EXISTS resource_availability_exceptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resource_id UUID NOT NULL REFERENCES resources(id),
    date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (resource_id, date)
);

CREATE INDEX IF NOT EXISTS idx_resource_availability_exceptions_resource_date
    ON resource_availability_exceptions(resource_id, date);

-- Appointments now point to a resource.
ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS resource_id UUID;

UPDATE appointments a
SET resource_id = r.id
FROM order_items oi
JOIN orders o ON o.id = oi.order_id
JOIN resources r
    ON r.store_id = o.store_id
   AND r.name = 'Default resource'
   AND r.type = 'PERSON'
WHERE a.order_item_id = oi.id
  AND a.resource_id IS NULL;

ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_resource
        FOREIGN KEY (resource_id)
            REFERENCES resources(id);

CREATE INDEX IF NOT EXISTS idx_appointments_resource
    ON appointments(resource_id);

CREATE INDEX IF NOT EXISTS idx_appointments_resource_status_scheduled
    ON appointments(resource_id, status, scheduled_at);

ALTER TABLE appointments
    ALTER COLUMN resource_id SET NOT NULL;
