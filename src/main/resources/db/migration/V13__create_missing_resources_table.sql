-- V13__create_missing_resources_table.sql
-- Repair migration: Creates resources table that was missed in V6

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

-- Seed one default resource per store
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
