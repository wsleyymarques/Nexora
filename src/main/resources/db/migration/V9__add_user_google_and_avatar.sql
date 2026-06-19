-- V9__add_user_google_and_avatar.sql

-- Add missing columns to users table from User entity
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(500);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS google_id VARCHAR(255) UNIQUE;

CREATE INDEX IF NOT EXISTS idx_users_google_id
    ON users(google_id);
