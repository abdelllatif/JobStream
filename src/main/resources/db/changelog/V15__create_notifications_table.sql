-- liquibase formatted sql

-- changeset jobstream:15
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    entity_id UUID, -- reference to message_id, application_id, etc.
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
