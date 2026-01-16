-- Initialize JobStream Database
-- Create database if not exists
CREATE DATABASE IF NOT EXISTS jobstream;

-- Connect to the database
\c jobstream;

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create tables (basic structure - Liquibase will handle the full schema)
-- This is just for initial setup

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    profile_picture VARCHAR(500),
    phone VARCHAR(20),
    bio TEXT,
    location VARCHAR(255),
    website VARCHAR(255),
    linkedin_profile VARCHAR(500),
    email_verified BOOLEAN DEFAULT FALSE,
    premium_user BOOLEAN DEFAULT FALSE,
    google_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert admin user (password: admin123)
INSERT INTO users (first_name, last_name, email, password, role, email_verified, premium_user, created_at, updated_at) 
VALUES ('Admin', 'User', 'admin@jobstream.com', '$2a$10$16$K8b9S2e7m3aF4c9h5d6e7f8g', 'ADMIN', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test candidate
INSERT INTO users (first_name, last_name, email, password, role, email_verified, premium_user, created_at, updated_at) 
VALUES ('John', 'Doe', 'john.doe@example.com', '$2a$10$16$K8b9S2e7m3aF4c9h5d6e7f8g', 'CANDIDATE', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test recruiter
INSERT INTO users (first_name, last_name, email, password, role, email_verified, premium_user, created_at, updated_at) 
VALUES ('Jane', 'Smith', 'jane.smith@example.com', '$2a$10$16$K8b9S2e7m3aF4c9h5d6e7f8g', 'RECRUITER', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

COMMIT;
