-- Liquibase formatted SQL
-- changeset istad:008-create-security-events-table

CREATE TABLE IF NOT EXISTS user_security_events (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    username VARCHAR(100) NOT NULL,
    category VARCHAR(30) NOT NULL, -- 'LOGIN' or 'ACTIVITY'
    event_type VARCHAR(50) NOT NULL, -- 'LOGIN_SUCCESS', 'LOGIN_FAILED', 'PASSWORD_CHANGED', 'USERNAME_CHANGED', 'PROFILE_UPDATED', 'IDENTITY_LINKED', 'IDENTITY_UNLINKED', 'SESSION_TERMINATED'
    status VARCHAR(20) NOT NULL, -- 'SUCCESS', 'FAILED'
    ip_address VARCHAR(100),
    device VARCHAR(150),
    location VARCHAR(150),
    auth_method VARCHAR(50), -- 'Password', 'Google SSO', 'GitHub SSO', 'Facebook SSO', 'Telegram'
    description VARCHAR(255),
    details JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_security_events_user_id ON user_security_events(user_id);
CREATE INDEX IF NOT EXISTS idx_security_events_username ON user_security_events(username);
CREATE INDEX IF NOT EXISTS idx_security_events_category ON user_security_events(category);
CREATE INDEX IF NOT EXISTS idx_security_events_created_at ON user_security_events(created_at DESC);
