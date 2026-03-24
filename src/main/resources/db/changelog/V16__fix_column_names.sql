-- liquibase formatted sql

-- changeset jobstream:16
ALTER TABLE companies RENAME COLUMN created_by_id TO created_by_user_id;
ALTER TABLE jobs RENAME COLUMN created_by_id TO created_by_user_id;
