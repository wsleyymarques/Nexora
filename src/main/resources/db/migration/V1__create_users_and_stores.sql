-- V1__create_users_and_stores.sql

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150)        NOT NULL,
    email       VARCHAR(255)        NOT NULL UNIQUE,
    password    VARCHAR(255)        NOT NULL,
    phone       VARCHAR(30),
    created_at  TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE TABLE stores (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150)        NOT NULL,
    slug        VARCHAR(150)        NOT NULL UNIQUE,
    description TEXT,
    active      BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE TABLE store_members (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID                NOT NULL REFERENCES users(id),
    store_id    UUID                NOT NULL REFERENCES stores(id),
    role        VARCHAR(20)         NOT NULL DEFAULT 'MEMBER',
    joined_at   TIMESTAMP           NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, store_id)
);

CREATE INDEX idx_store_members_user  ON store_members(user_id);
CREATE INDEX idx_store_members_store ON store_members(store_id);
