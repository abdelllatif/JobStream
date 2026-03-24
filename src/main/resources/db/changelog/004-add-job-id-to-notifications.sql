-- Changeset: 004-add-job-id-to-notifications.sql
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS job_id BIGINT;
ALTER TABLE notifications ADD CONSTRAINT fk_notifications_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE SET NULL;
