--liquibase formatted sql
--changeset iam:007-telegram-login-replay

CREATE TABLE telegram_login_replay (
    authentication_hash CHAR(64) PRIMARY KEY,
    expires_at TIMESTAMPTZ NOT NULL
);
