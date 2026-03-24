---
description: How to integrate the frontend with JobStream role-based scenarios
---

This workflow guides frontend developers through the core user scenarios based on roles (CANDIDATE, RECRUITER, ADMIN).

### 1. Unified Authentication Flow
1. **Login**: Use `POST /api/auth/login`. Store the `token` in LocalStorage or a Secure Cookie.
2. **Persistence**: On app initialization, call `GET /api/auth/me` to retrieve user profile and role.
3. **Route Guarding**: Use the `role` field from the auth response to hide/show navigation items (e.g., "Post a Job" only for RECRUITER).

### 2. Candidate Scenario (The Job Seeker)
- **Profile Setup**: Redirect new candidates to update their profile via `PUT /api/users/profile`.
- **CV Upload**: Use `POST /api/files/upload/cv` and store the returned URL in the candidate profile.
- **Job Search**: Implementation of the search bar should call `GET /api/search/jobs` with query params.
- **Application Logic**:
    - Check `isPremiumUser`. If false, count existing applications.
    - Call `POST /api/applications` with `jobId`.
- **Networking**: Integrate "Connect" buttons using `POST /api/connections/request`.

### 3. Recruiter Scenario (The Hiring Manager)
- **Company Branding**: Allow updating company details via `PUT /api/companies/{id}`.
- **Job Lifecycle**:
    - **Post**: `POST /api/jobs`.
    - **Close**: `PUT /api/jobs/{id}` with `active: false`.
- **Candidate Screening**:
    - Browse applicants per job: `GET /api/applications/job/{jobId}`.
    - Update candidate status: `PUT /api/applications/{id}` (Transitions: PENDING -> ACCEPTED/REJECTED).
- **Communication**: When accepting an application, encourage starting a chat via `POST /api/messages`.

### 4. Real-time Features (Shared)
- **Notifications**:
    - Polling: `GET /api/notifications/unread-count/{userId}` every 30s.
    - WebSockets: Connect to `/ws` for live STOMP updates on `APPLICATION_RECEIVED`, `MESSAGE_RECEIVED`, etc.
- **Chat**: Build the chat UI using `GET /api/messages/conversation?u2={partnerId}`.

### 5. Admin Scenario (System Dashboard)
- **Stats**: Use `/api/admin/stats` to render growth charts (Users, Jobs, Revenue).
- **Maintenance**: Provide a "Re-index Search" button that calls `POST /api/search/index`.
