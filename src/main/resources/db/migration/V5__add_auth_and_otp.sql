-- V5__add_auth_and_otp.sql

-- Users
ALTER TABLE users
    ALTER COLUMN email DROP NOT NULL;

ALTER TABLE users
    ALTER COLUMN password DROP NOT NULL;

ALTER TABLE users
    ALTER COLUMN phone SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT uk_users_phone UNIQUE (phone);

ALTER TABLE users
    ADD COLUMN verified BOOLEAN NOT NULL DEFAULT FALSE;


-- Customers
ALTER TABLE customers
    ADD COLUMN user_id UUID;

ALTER TABLE customers
    ADD CONSTRAINT fk_customers_user
        FOREIGN KEY (user_id)
            REFERENCES users(id);

CREATE INDEX idx_customers_user
    ON customers(user_id);


-- OTP Codes
CREATE TABLE otp_codes (
                           id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           target      VARCHAR(255) NOT NULL,
                           code        VARCHAR(6)   NOT NULL,
                           expires_at  TIMESTAMP    NOT NULL,
                           type        VARCHAR(20)  NOT NULL,
                           used        BOOLEAN      NOT NULL DEFAULT FALSE,
                           created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
                           updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_otp_target_type
    ON otp_codes(target, type);

CREATE INDEX idx_otp_expires_at
    ON otp_codes(expires_at);