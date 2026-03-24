-- liquibase formatted sql

-- changeset jobstream:17
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE;
UPDATE users SET updated_at = created_at WHERE updated_at IS NULL;
ALTER TABLE users ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE skills ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE;
UPDATE skills SET updated_at = created_at WHERE updated_at IS NULL;
ALTER TABLE skills ALTER COLUMN updated_at SET NOT NULL;
