-- liquibase formatted sql
-- changeset abdellatif:006-add-blocked-by-to-connections
ALTER TABLE connections ADD COLUMN blocked_by_id BIGINT;
ALTER TABLE connections ADD CONSTRAINT fk_connections_blocked_by FOREIGN KEY (blocked_by_id) REFERENCES users(id);
