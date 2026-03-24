-- liquibase formatted sql

-- changeset jobstream:18
ALTER TABLE users ADD COLUMN provider_id VARCHAR(255);
