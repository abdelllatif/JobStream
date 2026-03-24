-- liquibase formatted sql

-- changeset jobstream:11
CREATE TABLE user_blocks (
    id UUID PRIMARY KEY,
    blocker_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    blocked_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    UNIQUE(blocker_id, blocked_id)
);

CREATE INDEX idx_blocks_blocker_id ON user_blocks(blocker_id);
