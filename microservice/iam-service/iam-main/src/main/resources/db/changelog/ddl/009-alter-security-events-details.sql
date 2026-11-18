-- Liquibase formatted SQL
-- changeset istad:009-alter-security-events-details

ALTER TABLE user_security_events ALTER COLUMN details TYPE TEXT;
