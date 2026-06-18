-- V7__create_registered_clients.sql

CREATE TABLE IF NOT EXISTS registered_clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id UUID NOT NULL REFERENCES stores(id),
    name VARCHAR(100) NOT NULL,
    client_key_hash VARCHAR(255) NOT NULL UNIQUE,
    allowed_origin VARCHAR(255),
    allowed_ip VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_registered_clients_store
    ON registered_clients(store_id);

CREATE INDEX IF NOT EXISTS idx_registered_clients_origin_active
    ON registered_clients(allowed_origin, active);

CREATE INDEX IF NOT EXISTS idx_registered_clients_ip_active
    ON registered_clients(allowed_ip, active);
