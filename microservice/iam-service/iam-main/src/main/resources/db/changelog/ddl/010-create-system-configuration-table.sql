-- Liquibase formatted SQL
-- changeset istad:010-create-system-configuration-table

CREATE TABLE IF NOT EXISTS system_configurations (
    id BIGINT PRIMARY KEY,
    max_failed_attempts INT NOT NULL DEFAULT 5,
    lockout_duration_minutes INT NOT NULL DEFAULT 30,
    enable_auto_unlock BOOLEAN NOT NULL DEFAULT TRUE,
    mfa_policy VARCHAR(64) NOT NULL DEFAULT 'OPTIONAL',
    min_password_length INT NOT NULL DEFAULT 8,
    require_uppercase BOOLEAN NOT NULL DEFAULT TRUE,
    require_lowercase BOOLEAN NOT NULL DEFAULT TRUE,
    require_digits BOOLEAN NOT NULL DEFAULT TRUE,
    require_special_char BOOLEAN NOT NULL DEFAULT TRUE,
    password_expire_days INT NOT NULL DEFAULT 90,
    session_idle_timeout_minutes INT NOT NULL DEFAULT 30,
    session_max_inactive_days INT NOT NULL DEFAULT 7,
    max_concurrent_sessions INT NOT NULL DEFAULT 3,
    force_single_session BOOLEAN NOT NULL DEFAULT FALSE,
    access_token_ttl_hours INT NOT NULL DEFAULT 5,
    refresh_token_ttl_days INT NOT NULL DEFAULT 7,
    auth_code_ttl_minutes INT NOT NULL DEFAULT 3,
    reuse_refresh_tokens BOOLEAN NOT NULL DEFAULT FALSE,
    token_format VARCHAR(64) NOT NULL DEFAULT 'JWT',
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(128) DEFAULT 'SYSTEM'
);

INSERT INTO system_configurations (
    id,
    max_failed_attempts, lockout_duration_minutes, enable_auto_unlock, mfa_policy,
    min_password_length, require_uppercase, require_lowercase, require_digits, require_special_char, password_expire_days,
    session_idle_timeout_minutes, session_max_inactive_days, max_concurrent_sessions, force_single_session,
    access_token_ttl_hours, refresh_token_ttl_days, auth_code_ttl_minutes, reuse_refresh_tokens, token_format,
    updated_at, updated_by
) VALUES (
    1,
    5, 30, TRUE, 'OPTIONAL',
    8, TRUE, TRUE, TRUE, TRUE, 90,
    30, 7, 3, FALSE,
    5, 7, 3, FALSE, 'JWT',
    CURRENT_TIMESTAMP, 'SYSTEM'
) ON CONFLICT (id) DO NOTHING;
