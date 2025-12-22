-- USERS
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50) CHECK (role IN ('CANDIDATE','RECRUITER','ADMIN')),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
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

-- COMPANIES
CREATE TABLE IF NOT EXISTS companies (
                                         id BIGSERIAL PRIMARY KEY,
                                         name VARCHAR(255),
    description TEXT,
    website VARCHAR(255),
    logo_url VARCHAR(500),
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE
    );

-- DOMAINS
CREATE TABLE IF NOT EXISTS domains (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(255)
    );

-- JOBS
CREATE TABLE IF NOT EXISTS jobs (
                                    id BIGSERIAL PRIMARY KEY,
                                    title VARCHAR(255),
    description TEXT,
    location VARCHAR(255),
    contract_type VARCHAR(100),
    posted_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    company_id BIGINT REFERENCES companies(id) ON DELETE SET NULL,
    domain_id BIGINT REFERENCES domains(id) ON DELETE SET NULL
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
                                        sender_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    receiver_id BIGINT REFERENCES users(id) ON DELETE SET NULL
    );

-- NOTIFICATIONS
CREATE TABLE IF NOT EXISTS notifications (
                                             id BIGSERIAL PRIMARY KEY,
                                             message TEXT,
                                             read BOOLEAN DEFAULT FALSE,
                                             created_at TIMESTAMP,
                                             candidate_profile_id BIGINT REFERENCES candidate_profiles(id) ON DELETE CASCADE
    );

-- PAYMENTS
CREATE TABLE IF NOT EXISTS payments (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    paid_at TIMESTAMP,
    paypal_transaction_id VARCHAR(255),
    success BOOLEAN DEFAULT FALSE
    );
