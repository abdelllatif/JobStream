-- liquibase formatted sql

-- changeset jobstream:10
CREATE TABLE connections (
    id UUID PRIMARY KEY,
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    UNIQUE(sender_id, receiver_id)
);

CREATE INDEX idx_connections_sender_id ON connections(sender_id);
CREATE INDEX idx_connections_receiver_id ON connections(receiver_id);
