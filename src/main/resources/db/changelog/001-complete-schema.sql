-- Liquibase changelog for complete database schema
-- Changeset: 001-complete-schema.sql

-- USERS
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50) CHECK (role IN ('CANDIDATE','RECRUITER','ADMIN','CEO')),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    profile_picture VARCHAR(500),
    phone VARCHAR(20),
    bio TEXT,
    location VARCHAR(255),
    website VARCHAR(255),
    linkedin_profile VARCHAR(500),
    email_verified BOOLEAN DEFAULT FALSE,
    premium_user BOOLEAN DEFAULT FALSE,
    google_id VARCHAR(255)
    );

-- CANDIDATE PROFILES
CREATE TABLE IF NOT EXISTS candidate_profiles (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    phone VARCHAR(100),
    address VARCHAR(255),
    summary TEXT,
    cv_url VARCHAR(500)
    );

-- COMPANIES (without foreign key to domains first)
CREATE TABLE IF NOT EXISTS companies (
                                         id BIGSERIAL PRIMARY KEY,
                                         name VARCHAR(255),
    description TEXT,
    website VARCHAR(255),
    logo_url VARCHAR(500),
    owner_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    domain_id BIGINT
    );

-- DOMAINS
CREATE TABLE IF NOT EXISTS domains (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(255)
    );

-- JOBS (without foreign keys first)
CREATE TABLE IF NOT EXISTS jobs (
                                    id BIGSERIAL PRIMARY KEY,
                                    title VARCHAR(255),
    description TEXT,
    location VARCHAR(255),
    contract_type VARCHAR(100),
    posted_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    company_id BIGINT,
    domain_id BIGINT
    );

-- TAGS
CREATE TABLE IF NOT EXISTS tags (
                                    id BIGSERIAL PRIMARY KEY,
                                    name VARCHAR(100)
    );

-- JOB_TAGS (Join table)
CREATE TABLE IF NOT EXISTS job_tags (
                                        job_id BIGINT REFERENCES jobs(id) ON DELETE CASCADE,
    tag_id BIGINT REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (job_id, tag_id)
    );

-- APPLICATIONS
CREATE TABLE IF NOT EXISTS applications (
                                            id BIGSERIAL PRIMARY KEY,
                                            candidate_profile_id BIGINT REFERENCES candidate_profiles(id) ON DELETE CASCADE,
    job_id BIGINT REFERENCES jobs(id) ON DELETE CASCADE,
    status VARCHAR(50) CHECK (status IN ('PENDING','ACCEPTED','REJECTED')),
    applied_at TIMESTAMP
    );

-- EDUCATIONS
CREATE TABLE IF NOT EXISTS educations (
                                          id BIGSERIAL PRIMARY KEY,
                                          school VARCHAR(255),
    degree VARCHAR(255),
    start_date DATE,
    end_date DATE,
    candidate_profile_id BIGINT REFERENCES candidate_profiles(id) ON DELETE CASCADE
    );

-- EXPERIENCES
CREATE TABLE IF NOT EXISTS experiences (
                                           id BIGSERIAL PRIMARY KEY,
                                           title VARCHAR(255),
    company VARCHAR(255),
    start_date DATE,
    end_date DATE,
    description TEXT,
    candidate_profile_id BIGINT REFERENCES candidate_profiles(id) ON DELETE CASCADE
    );

-- SKILLS
CREATE TABLE IF NOT EXISTS skills (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(100),
    level INTEGER,
    candidate_profile_id BIGINT REFERENCES candidate_profiles(id) ON DELETE CASCADE
    );

-- MESSAGES
CREATE TABLE IF NOT EXISTS messages (
                                        id BIGSERIAL PRIMARY KEY,
                                        content TEXT,
                                        sent_at TIMESTAMP,
                                        read BOOLEAN DEFAULT FALSE,
                                        read_at TIMESTAMP,
                                        sender_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    receiver_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    job_id BIGINT REFERENCES jobs(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- NOTIFICATIONS
CREATE TABLE IF NOT EXISTS notifications (
                                             id BIGSERIAL PRIMARY KEY,
                                             title VARCHAR(255),
    message TEXT,
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    candidate_profile_id BIGINT REFERENCES candidate_profiles(id) ON DELETE CASCADE,
    type VARCHAR(50) DEFAULT 'MESSAGE_RECEIVED'
    );

-- PAYMENTS
CREATE TABLE IF NOT EXISTS payments (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    amount DECIMAL(10,2),
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(50) DEFAULT 'PENDING',
    plan_type VARCHAR(50),
    paypal_order_id VARCHAR(255),
    paypal_payment_id VARCHAR(255),
    paypal_transaction_id VARCHAR(255),
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- CONNECTIONS
CREATE TABLE IF NOT EXISTS connections (
                                           id BIGSERIAL PRIMARY KEY,
                                           requester_id BIGINT NOT NULL,
                                           receiver_id BIGINT NOT NULL,
                                           status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMP NOT NULL,
    accepted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_connections_requester FOREIGN KEY (requester_id) REFERENCES users(id),
    CONSTRAINT fk_connections_receiver FOREIGN KEY (receiver_id) REFERENCES users(id),
    CONSTRAINT chk_connections_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'BLOCKED')),
    CONSTRAINT uq_connections_unique UNIQUE (requester_id, receiver_id)
    );

-- PREMIUM SUBSCRIPTIONS
CREATE TABLE IF NOT EXISTS premium_subscriptions (
                                                     id BIGSERIAL PRIMARY KEY,
                                                     user_id BIGINT NOT NULL,
                                                     plan_type VARCHAR(50) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    payment_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_premium_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_premium_subscriptions_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
    );

-- PROFILE VISITS
CREATE TABLE IF NOT EXISTS profile_visits (
                                              id BIGSERIAL PRIMARY KEY,
                                              visitor_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    profile_id BIGINT REFERENCES candidate_profiles(id) ON DELETE CASCADE,
    visited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- COMPANY USERS
CREATE TABLE IF NOT EXISTS company_users (
                                             id BIGSERIAL PRIMARY KEY,
                                             user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    company_id BIGINT REFERENCES companies(id) ON DELETE CASCADE,
    job_title VARCHAR(255),
    joined_at DATE,
    status VARCHAR(50) DEFAULT 'PENDING'
    );

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_candidate_profiles_user_id ON candidate_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_companies_owner_id ON companies(owner_id);
CREATE INDEX IF NOT EXISTS idx_companies_domain_id ON companies(domain_id);
CREATE INDEX IF NOT EXISTS idx_domains_name ON domains(name);
CREATE INDEX IF NOT EXISTS idx_jobs_company_id ON jobs(company_id);
CREATE INDEX IF NOT EXISTS idx_jobs_domain_id ON jobs(domain_id);
CREATE INDEX IF NOT EXISTS idx_jobs_active ON jobs(active);
CREATE INDEX IF NOT EXISTS idx_tags_name ON tags(name);
CREATE INDEX IF NOT EXISTS idx_applications_candidate_id ON applications(candidate_profile_id);
CREATE INDEX IF NOT EXISTS idx_applications_job_id ON applications(job_id);
CREATE INDEX IF NOT EXISTS idx_applications_status ON applications(status);
CREATE INDEX IF NOT EXISTS idx_educations_candidate_id ON educations(candidate_profile_id);
CREATE INDEX IF NOT EXISTS idx_experiences_candidate_id ON experiences(candidate_profile_id);
CREATE INDEX IF NOT EXISTS idx_skills_candidate_id ON skills(candidate_profile_id);
CREATE INDEX IF NOT EXISTS idx_skills_name ON skills(name);
CREATE INDEX IF NOT EXISTS idx_messages_sender_id ON messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_messages_receiver_id ON messages(receiver_id);
CREATE INDEX IF NOT EXISTS idx_messages_job_id ON messages(job_id);
CREATE INDEX IF NOT EXISTS idx_messages_read ON messages(read);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(type);
CREATE INDEX IF NOT EXISTS idx_notifications_read ON notifications(read);
CREATE INDEX IF NOT EXISTS idx_payments_user_id ON payments(user_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_connections_requester_id ON connections(requester_id);
CREATE INDEX IF NOT EXISTS idx_connections_receiver_id ON connections(receiver_id);
CREATE INDEX IF NOT EXISTS idx_connections_status ON connections(status);
CREATE INDEX IF NOT EXISTS idx_premium_subscriptions_user_id ON premium_subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_premium_subscriptions_active ON premium_subscriptions(active);
CREATE INDEX IF NOT EXISTS idx_premium_subscriptions_end_date ON premium_subscriptions(end_date);
CREATE INDEX IF NOT EXISTS idx_profile_visits_visitor_id ON profile_visits(visitor_id);
CREATE INDEX IF NOT EXISTS idx_profile_visits_profile_id ON profile_visits(profile_id);
CREATE INDEX IF NOT EXISTS idx_profile_visits_visited_at ON profile_visits(visited_at);
CREATE INDEX IF NOT EXISTS idx_company_users_user_id ON company_users(user_id);
CREATE INDEX IF NOT EXISTS idx_company_users_company_id ON company_users(company_id);
CREATE INDEX IF NOT EXISTS idx_company_users_status ON company_users(status);

-- Add foreign key constraints after all tables are created (only if they don't exist)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_companies_domain') THEN
ALTER TABLE companies ADD CONSTRAINT fk_companies_domain FOREIGN KEY (domain_id) REFERENCES domains(id) ON DELETE SET NULL;
END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_jobs_company') THEN
ALTER TABLE jobs ADD CONSTRAINT fk_jobs_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL;
END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_jobs_domain') THEN
ALTER TABLE jobs ADD CONSTRAINT fk_jobs_domain FOREIGN KEY (domain_id) REFERENCES domains(id) ON DELETE SET NULL;
END IF;
END $$;