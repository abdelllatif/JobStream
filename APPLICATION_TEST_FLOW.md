# JobStream Application Test Flow

This document outlines the logical sequence for testing the JobStream recruitment platform, following the natural dependencies between different features.

## Phase 1: Foundation - User Management & Authentication

### 1.1 User Registration
**Endpoint**: `POST /api/auth/register`
**Purpose**: Create the basic user accounts needed for all other operations
**Order**: First - All other operations require authenticated users

**Test Scenarios**:
- Register Candidate user (role: CANDIDATE)
- Register Company user (role: COMPANY)
- Register Recruiter user (role: RECRUITER)

**Dependencies**: None

### 1.2 User Authentication
**Endpoint**: `POST /api/auth/login`
**Purpose**: Obtain JWT tokens for subsequent API calls
**Order**: Second - Required for all protected endpoints

**Test Scenarios**:
- Login with valid credentials for each user type
- Store tokens for use in subsequent tests

**Dependencies**: User registration completed

## Phase 2: Core Data Setup

### 2.1 Company Management
**Endpoint**: `POST /api/companies`
**Purpose**: Create company profiles required for job postings
**Order**: Third - Jobs must belong to companies

**Test Scenarios**:
- Create company profile for company users
- Update company information
- Upload company logo

**Dependencies**: Authenticated company users

### 2.2 Domain & Skills Setup
**Endpoint**: `GET /api/domains`, `GET /api/skills`
**Purpose**: Retrieve available domains and skills for job categorization
**Order**: Fourth - Required for job creation

**Test Scenarios**:
- List all available domains
- List all available skills

**Dependencies**: None (public endpoints)

### 2.3 User Profile Enhancement
**Endpoints**: 
- `POST /api/files/upload-cv`
- `POST /api/files/upload-profile-picture`
- `PUT /api/candidates/profile`
- `PUT /api/recruiters/profile`

**Purpose**: Complete user profiles for better matching
**Order**: Fifth - Enhances job application and search functionality

**Test Scenarios**:
- Upload CV for candidates
- Upload profile pictures
- Complete candidate profiles with education and experience
- Complete recruiter profiles

**Dependencies**: Authenticated users

## Phase 3: Job Management

### 3.1 Job Creation
**Endpoint**: `POST /api/jobs`
**Purpose**: Create job postings for candidates to apply to
**Order**: Sixth - Core functionality of the platform

**Test Scenarios**:
- Create job with valid company and domain
- Create jobs with different contract types (CDI, CDD, Alternance, Stage)
- Create remote and on-site jobs

**Dependencies**: 
- Authenticated company/recruiter users
- Existing company profiles
- Available domains

### 3.2 Job Search & Discovery
**Endpoints**: 
- `GET /api/jobs`
- `GET /api/jobs/search`
- `GET /api/search/jobs`
- `GET /api/search/jobs/advanced`

**Purpose**: Test job discovery functionality
**Order**: Seventh - Core candidate functionality

**Test Scenarios**:
- Browse all jobs with pagination
- Search jobs by keyword
- Filter by location, contract type, salary
- Advanced search with multiple filters

**Dependencies**: Jobs exist in the system

## Phase 4: Application Process

### 4.1 Job Applications
**Endpoint**: `POST /api/applications`
**Purpose**: Allow candidates to apply for jobs
**Order**: Eighth - Main business process

**Test Scenarios**:
- Apply to jobs with cover letter
- Apply to multiple jobs
- Apply with uploaded CV

**Dependencies**:
- Authenticated candidates
- Available jobs
- Complete candidate profiles

### 4.2 Application Management
**Endpoints**: 
- `GET /api/applications/job/{jobId}`
- `GET /api/applications/candidate/{candidateId}`
- `PUT /api/applications/{id}/status`

**Purpose**: Manage application lifecycle
**Order**: Ninth - Recruiter functionality

**Test Scenarios**:
- View applications for posted jobs
- View candidate's application history
- Update application status (PENDING, ACCEPTED, REJECTED)

**Dependencies**: Job applications exist

## Phase 5: Social Features

### 5.1 Network Connections
**Endpoints**: 
- `POST /api/connections/request`
- `PUT /api/connections/accept/{id}`
- `PUT /api/connections/reject/{id}`
- `GET /api/connections/user/{userId}`

**Purpose**: Build professional network
**Order**: Tenth - Social networking features

**Test Scenarios**:
- Send connection requests
- Accept/reject requests
- View connections

**Dependencies**: Authenticated users

### 5.2 Messaging System
**Endpoints**: 
- `POST /api/messages/send`
- `GET /api/messages/conversation/{userId1}/{userId2}`
- `GET /api/messages/user/{userId}`

**Purpose**: Enable communication between connected users
**Order**: Eleventh - Communication features

**Test Scenarios**:
- Send messages to connections
- View conversations
- Check unread messages

**Dependencies**: Established connections

## Phase 6: Premium Features

### 6.1 Premium Subscriptions
**Endpoints**: 
- `POST /api/premium/subscribe`
- `GET /api/premium/active/{userId}`
- `GET /api/premium/check/{userId}`

**Purpose**: Test premium functionality
**Order**: Twelfth - Monetization features

**Test Scenarios**:
- Subscribe to premium plans
- Check premium status
- Access premium-only features

**Dependencies**: Authenticated users, payment setup

### 6.2 Payment Processing
**Endpoints**: 
- `POST /api/payments/create`
- `POST /api/payments/execute/{paymentId}`

**Purpose**: Handle payment transactions
**Order**: Thirteenth - Payment integration

**Test Scenarios**:
- Create payment for premium subscription
- Execute payment
- View payment history

**Dependencies**: Premium subscription attempts

## Phase 7: Analytics & Insights

### 7.1 Profile Analytics
**Endpoints**: 
- `POST /api/profile-visits/record`
- `GET /api/profile-visits/profile/{profileId}`

**Purpose**: Track profile visibility
**Order**: Fourteenth - Analytics features

**Test Scenarios**:
- Record profile visits
- View visit statistics
- Track recent visitors

**Dependencies**: User profiles exist

### 7.2 Notifications
**Endpoints**: 
- `GET /api/notifications/user/{userId}`
- `PUT /api/notifications/read/{id}`

**Purpose**: User notification system
**Order**: Fifteenth - User engagement

**Test Scenarios**:
- View notifications
- Mark as read
- Test notification triggers

**Dependencies**: Various user actions (applications, connections, etc.)

## Phase 8: Advanced Features

### 8.1 Recommendation Engine
**Endpoint**: `GET /api/search/jobs/recommended/{userId}`
**Purpose**: Test job recommendations
**Order**: Sixteenth - AI-powered features

**Test Scenarios**:
- Get recommended jobs for candidates
- Test recommendation accuracy

**Dependencies**: User profiles, job data, application history

### 8.2 Search Suggestions
**Endpoint**: `GET /api/search/suggestions`
**Purpose**: Test autocomplete functionality
**Order**: Seventeenth - UX enhancement

**Test Scenarios**:
- Get skill suggestions
- Get location suggestions
- Test search autocomplete

**Dependencies**: Sufficient data in the system

## Critical Dependencies Summary

1. **Authentication** → All protected endpoints
2. **Company Profiles** → Job creation
3. **Jobs** → Applications, search, recommendations
4. **User Profiles** → Applications, connections, recommendations
5. **Applications** → Notifications, analytics
6. **Connections** → Messaging, network features
7. **Payments** → Premium features

## Testing Best Practices

1. **Use the same user IDs** across all test phases for consistency
2. **Clean up test data** between test runs
3. **Test error scenarios** (invalid data, unauthorized access)
4. **Verify data integrity** at each phase
5. **Test pagination** for list endpoints
6. **Validate business rules** (e.g., candidates can't create jobs)
7. **Test file uploads** with different file formats
8. **Verify notification triggers** for key actions

## Environment Setup

- **Base URL**: `http://localhost:8080`
- **Authentication**: Bearer tokens stored in Postman variables
- **Test Data**: Create consistent test users and companies
- **File Storage**: Ensure upload directories exist and have proper permissions

This flow ensures that all dependencies are respected and features are tested in a logical order that mirrors real-world usage patterns.
