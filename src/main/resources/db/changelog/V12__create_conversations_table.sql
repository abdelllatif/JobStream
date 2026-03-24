-- liquibase formatted sql

-- changeset jobstream:12
CREATE TABLE conversations (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE
);
