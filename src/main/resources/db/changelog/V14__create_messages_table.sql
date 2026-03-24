-- liquibase formatted sql

-- changeset jobstream:14
CREATE TABLE messages (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    job_id UUID REFERENCES jobs(id), -- optional job reference
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
