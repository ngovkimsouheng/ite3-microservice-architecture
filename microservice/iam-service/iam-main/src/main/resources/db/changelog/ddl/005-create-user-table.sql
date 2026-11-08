-- Liquibase formatted SQL
-- changeset istad:005-create-user-table

CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     uuid UUID NOT NULL UNIQUE,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    password_hash VARCHAR(255),
    given_name VARCHAR(100) NOT NULL,
    family_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    nickname VARCHAR(100),
    gender VARCHAR(20),
    birthdate DATE,
    profile_url VARCHAR(500),
    picture_url VARCHAR(500),
    website_url VARCHAR(500),
    zoneinfo VARCHAR(50),
    locale VARCHAR(10) NOT NULL DEFAULT 'en',
    phone_number VARCHAR(20) UNIQUE,
    phone_number_verified BOOLEAN NOT NULL DEFAULT FALSE,
    address_street_address VARCHAR(255),
    address_locality VARCHAR(100),
    address_region VARCHAR(100),
    address_postal_code VARCHAR(20),
    address_country CHAR(2),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    last_login_at TIMESTAMPTZ,
    password_changed_at TIMESTAMPTZ,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    version BIGINT NOT NULL DEFAULT 0
    );

-- Linked social accounts (B.4)
CREATE TABLE IF NOT EXISTS identities (
                                          id BIGSERIAL PRIMARY KEY,
                                          uuid UUID NOT NULL UNIQUE,
                                          user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider VARCHAR(20) NOT NULL,
    provider_subject VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    display_name VARCHAR(150),
    username VARCHAR(100),
    picture_url VARCHAR(500),
    attributes JSONB,
    linked_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMPTZ,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_identity_provider_subject UNIQUE (provider, provider_subject)
    );

-- Audited join table for User <-> Role (B.5)
CREATE TABLE IF NOT EXISTS user_roles (
                                          id BIGSERIAL PRIMARY KEY,
                                          user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    expires_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,
    revoked_by VARCHAR(100),
    reason VARCHAR(255),
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
    );

-- Audited join table for User <-> Permission (B.5)
CREATE TABLE IF NOT EXISTS user_permissions (
                                                id BIGSERIAL PRIMARY KEY,
                                                user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    effect VARCHAR(5) NOT NULL CHECK (effect IN ('GRANT', 'DENY')),
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    expires_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,
    revoked_by VARCHAR(100),
    reason VARCHAR(255),
    CONSTRAINT uk_user_permission UNIQUE (user_id, permission_id)
    );

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_identities_user_id ON identities(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_permissions_user_id ON user_permissions(user_id);