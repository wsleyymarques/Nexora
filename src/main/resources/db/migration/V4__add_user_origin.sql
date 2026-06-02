-- V4__add_user_origin.sql

ALTER TABLE users
    ADD COLUMN origin VARCHAR(20) NOT NULL DEFAULT 'DIRECT';
