-- V10__implement_resource_availability_exceptions.sql

-- Recreate resource_availability_exceptions table with all required fields
-- mapped from ResourceAvailabilityException entity

DROP TABLE IF EXISTS resource_availability_exceptions CASCADE;

CREATE TABLE resource_availability_exceptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resource_id UUID NOT NULL REFERENCES resources(id) ON DELETE CASCADE,
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

CREATE INDEX IF NOT EXISTS idx_resource_availability_exceptions_available
    ON resource_availability_exceptions(resource_id, available);
