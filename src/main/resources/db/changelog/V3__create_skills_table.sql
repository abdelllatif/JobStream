-- liquibase formatted sql

-- changeset jobstream:3
CREATE TABLE skills (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_skills_user_id ON skills(user_id);
