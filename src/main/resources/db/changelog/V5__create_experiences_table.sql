-- liquibase formatted sql

-- changeset jobstream:5
CREATE TABLE experiences (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id UUID, -- allows null if company not registered
    title VARCHAR(255) NOT NULL,
    employment_type VARCHAR(50),
    start_date DATE,
    end_date DATE,
    is_current BOOLEAN DEFAULT FALSE,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_experiences_user_id ON experiences(user_id);
