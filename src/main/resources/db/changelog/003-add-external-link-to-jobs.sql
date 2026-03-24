-- Changeset: 003-add-external-link-to-jobs.sql
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS external_link VARCHAR(500);
