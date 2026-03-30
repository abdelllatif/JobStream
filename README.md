# JobStream — Complete Architecture Breakdown

## Project Summary

**JobStream** is a LinkedIn-like recruitment platform built with **Spring Boot 3**, **PostgreSQL**, **Liquibase** (migrations), **JWT + Google OAuth2** authentication, **WebSocket/STOMP** real-time messaging, and **Swagger/OpenAPI** documentation.

---

## Group 1 — Authentication & Security

### Endpoints (`/api/auth`)

| Method | Path | Access | What it does |
|--------|------|--------|--------------|
| `POST` | `/api/auth/register` | Public | Creates a new user; returns `UserResponse` |
| `POST` | `/api/auth/login` | Public | Validates credentials; returns `access_token` + `refresh_token` |
| `POST` | `/api/auth/refresh-token` | Public | Re-issues a new access token using a valid refresh token |
| `GET` | `/api/auth/google` | Public | Redirects browser to Google's OAuth2 authorization page |
| `GET` | `/login/oauth2/code/google` | Spring-internal | Google callback handled automatically by Spring Security OAuth2 |

### Files & Why They Exist

| File | Purpose |
|------|---------|
| `SecurityConfig.java` | Central HTTP security configuration — sets up JWT filter, OAuth2 login, CORS, public/protected route rules, stateless session policy |
| `JwtAuthenticationFilter.java` | Servlet filter — reads `Authorization: Bearer <token>` on every HTTP request, validates it, and loads the user into `SecurityContextHolder` |
| `JwtService.java` | Issues and validates JWT tokens; embeds `role` and `userId` as custom claims; handles both access token and refresh token generation |
| `JwtProperties.java` | `@ConfigurationProperties` binding for `application.jwt.*` (secret, expiration durations) — avoids hardcoding values |
| `CustomUserDetailsService.java` | Spring Security hook — loads `User` from DB by email so the filter and authentication manager can verify credentials |
| `GoogleOAuth2SuccessHandler.java` | Called after a successful Google login — creates a new user or links Google to an existing account, then redirects to the frontend with the JWT in the URL |
| `HttpCookieOAuth2AuthorizationRequestRepository.java` | Stores the OAuth2 authorization state in a short-lived cookie instead of an HTTP session (required because the app is fully stateless — no sessions) |
| `CookieUtils.java` | Helper for serializing/deserializing objects into cookies (used only by the OAuth2 repository above) |
| `WebSocketSecurityConfig.java` | Intercepts STOMP `CONNECT` frames — validates the JWT from the `Authorization` header and sets the authenticated principal on the WebSocket session |
| `AuthController.java` | REST controller exposing the 4 auth endpoints |
| `AuthService` / `AuthServiceImpl` | Business logic for register, login, refresh — password hashing with BCrypt, token generation |

### How Security Works (flow)

```
Request → JwtAuthenticationFilter
              ↓ extracts Bearer token
              ↓ JwtService.isTokenValid()
              ↓ CustomUserDetailsService.loadUserByUsername()
              ↓ sets SecurityContextHolder
         → Spring's authorization rules (permitAll vs authenticated)
         → @PreAuthorize checks (admin-only endpoints)
```

**Role system:** only `ADMIN` and `USER`. `@PreAuthorize("hasRole('ADMIN')")` is used for admin endpoints.

**Provider system:** `LOCAL` (email+password), `GOOGLE` (OAuth2 only), `LOCAL_GOOGLE` (account linked to both).

---

## Group 2 — User Management

### Endpoints (`/api/users`)

| Method | Path | Access | What it does |
|--------|------|--------|--------------|
| `GET` | `/api/users/me` | Auth | Returns the currently logged-in user's info |
| `GET` | `/api/users/{id}` | Auth | Fetch any user by UUID |
| `GET` | `/api/users/search?query=` | Auth | Search by email/headline; excludes users who blocked you |
| `GET` | `/api/users/network` | Auth | Suggested users for networking (excludes admins and blocked users) |
| `GET` | `/api/users/all` | **ADMIN only** | All non-admin users (paginated) |
| `GET` | `/api/users/me/has-password` | Auth | Check if a Google-only account has set a password yet |
| `POST` | `/api/users/me/set-password` | Auth | Google users with no password can set one |
| `PUT` | `/api/users/me/change-password` | Auth | Change existing password (requires old password) |
| `PUT` | `/api/users/{id}/activate` | **ADMIN only** | Re-enable a disabled account |
| `DELETE` | `/api/users/{id}` | Auth (owner or admin) | Soft-delete — sets `enabled = false` |
| `PUT` | `/api/users/{id}/role` | **ADMIN only** | Promote/demote user roles |

### Files

| File | Purpose |
|------|---------|
| `UserController.java` | REST layer for all user operations |
| `UserService` / `UserServiceImpl` | Business logic — ownership checks, password hashing, blocking-aware search |
| `UserRepository.java` | JPA repository with custom queries for search and network suggestions |
| `User.java` (entity) | Core user entity — implements `UserDetails` directly so Spring Security can use it as the principal |
| `UserMapper.java` | Converts `User` → `UserResponse` DTO |
| `UserResponse.java` | What gets returned on every user endpoint |
| `ChangePasswordRequest`, `SetPasswordRequest`, `UpdateRoleRequest` | Input DTOs with validation annotations |

---

## Group 3 — Profile, Skills, Education, Experience & File Uploads

### Endpoints

| Method | Path | Access | What it does |
|--------|------|--------|--------------|
| `GET` | `/api/profiles/{userId}` | Auth | Get someone's profile |
| `GET` | `/api/profiles/me` | Auth | Get your own profile |
| `PUT` | `/api/profiles` | Auth | Create or update your profile (bio, headline, etc.) |
| `POST` | `/api/profiles/photo` | Auth | Upload profile photo (multipart) |
| `POST` | `/api/profiles/cv` | Auth | Upload CV/resume (multipart) |
| `GET` | `/api/skills/user/{userId}` | Auth | Get skills of a user |
| `GET` | `/api/skills/me` | Auth | Get your own skills |
| `POST` | `/api/skills` | Auth | Add a skill |
| `DELETE` | `/api/skills/{skillId}` | Auth | Delete a skill you own |
| `GET` | `/api/educations/user/{userId}` | Auth | Get education list for a user |
| `GET` | `/api/educations/me` | Auth | Get your own education |
| `POST` | `/api/educations` | Auth | Add an education entry |
| `PUT` | `/api/educations/{id}` | Auth | Update an education entry |
| `DELETE` | `/api/educations/{id}` | Auth | Delete an education entry |
| `GET` | `/api/experiences/user/{userId}` | Auth | Get experience list for a user |
| `GET` | `/api/experiences/me` | Auth | Get your own experience |
| `POST` | `/api/experiences` | Auth | Add a work experience entry |
| `PUT` | `/api/experiences/{id}` | Auth | Update an experience entry |
| `DELETE` | `/api/experiences/{id}` | Auth | Delete an experience entry |

### Files

| File | Purpose |
|------|---------|
| `Profile.java` (entity) | One-to-one with `User` — stores headline, bio, photo URL, CV URL |
| `Skill.java` | Many-to-one with `User` — a single skill with name |
| `Education.java` | Many-to-one with `User` — school, degree, dates |
| `Experience.java` | Many-to-one with `User` — company, role, dates |
| `ProfileService` / `ProfileServiceImpl` | Create/update profile, delegates file upload to `FileStorageService` |
| `SkillService` / `SkillServiceImpl` | Add/delete skills with ownership check |
| `EducationService` / `EducationServiceImpl` | CRUD education entries with ownership check |
| `ExperienceService` / `ExperienceServiceImpl` | CRUD experience entries with ownership check |
| `FileStorageService` / `FileStorageServiceImpl` | Stores uploaded files to disk with UUID-based filenames to avoid collisions |
| `FileStorageProperties.java` | Binds `application.file.storage-path` and `base-url` from config |
| `WebConfig.java` | Serves the `uploads/` folder as static resources over HTTP at `/uploads/**` |
| `ProfileMapper`, `SkillMapper`, `EducationMapper`, `ExperienceMapper` | Entity → DTO conversions |

---

## Group 4 — Company Management

### Endpoints (`/api/companies`)

| Method | Path | Access | What it does |
|--------|------|--------|--------------|
| `POST` | `/api/companies` | Auth | Register a new company (you become OWNER) |
| `PUT` | `/api/companies/{id}` | Auth (member) | Update company info |
| `GET` | `/api/companies/{id}` | **Public** | Get company details |
| `GET` | `/api/companies/search?query=` | **Public** | Search companies by name (paginated) |
| `GET` | `/api/companies/my` | Auth | Get all companies you belong to |
| `DELETE` | `/api/companies/{id}` | Auth (owner) | Delete a company |
| `POST` | `/api/companies/{id}/logo` | Auth (member) | Upload company logo |
| `POST` | `/api/companies/{id}/employees` | Auth (member) | Add an employee by user ID + role |
| `DELETE` | `/api/companies/{companyId}/employees/{memberId}` | Auth (owner) | Remove an employee |
| `GET` | `/api/companies/{id}/employees` | **Public** | List all employees of a company |

### Files

| File | Purpose |
|------|---------|
| `Company.java` | Entity — name, description, domain, logo URL |
| `CompanyUser.java` | Junction entity — maps users to companies with a `CompanyRole` |
| `CompanyRole.java` | Enum — only `OWNER` (the role system is minimal; planned for expansion) |
| `CompanyRepository`, `CompanyUserRepository` | JPA data access |
| `CompanyService` / `CompanyServiceImpl` | Business logic — ownership checks before update/delete, logo upload delegation |
| `CompanyController.java` | REST controller |
| `CompanyMapper`, `UserMapper` | Entity → DTO conversions |
| `AddCompanyEmployeeRequest`, `CompanyRequest` | Input DTOs |
| `CompanyResponse`, `CompanyUserResponse` | Output DTOs |

---

## Group 5 — Job Board

### Endpoints (`/api/jobs`)

| Method | Path | Access | What it does |
|--------|------|--------|--------------|
| `POST` | `/api/jobs` | Auth | Post a new job under a company |
| `PUT` | `/api/jobs/{id}` | Auth (poster) | Update a job |
| `GET` | `/api/jobs/{id}` | **Public** | Get a specific job |
| `GET` | `/api/jobs/search` | **Public** | Search jobs by keyword, location, type, status (paginated) |
| `GET` | `/api/jobs/except-poster` | Auth | Get all jobs not posted by you (for job seekers) |
| `GET` | `/api/jobs/company/{companyId}` | **Public** | Get all open jobs for a company |
| `DELETE` | `/api/jobs/{id}` | Auth (poster) | Delete a job |

### Files

| File | Purpose |
|------|---------|
| `Job.java` | Entity — title, description, location, type, status, company |
| `JobStatus.java` | Enum: `OPEN`, `CLOSED` |
| `JobType.java` | Enum: e.g. `FULL_TIME`, `PART_TIME`, `REMOTE`, `INTERNSHIP` |
| `JobRepository.java` | JPA with custom search query using keyword/location/type/status filters |
| `JobService` / `JobServiceImpl` | Business logic, ownership verification for mutations |
| `JobController.java` | REST controller |
| `JobMapper.java` | Entity → DTO |
| `JobRequest`, `JobResponse`, `PageResponse` | DTOs |

---

## Group 6 — Job Applications

### Endpoints (`/api/applications`)

| Method | Path | Access | What it does |
|--------|------|--------|--------------|
| `POST` | `/api/applications` | Auth | Apply to a job (with optional CV URL or cover letter) |
| `GET` | `/api/applications/my` | Auth | Get all your own applications (paginated) |
| `GET` | `/api/applications/job/{jobId}` | Auth (company member) | See all applications for a job |
| `PUT` | `/api/applications/{id}/status` | Auth (company member) | Change application status (PENDING → ACCEPTED/REJECTED) |
| `DELETE` | `/api/applications/{id}` | Auth (applicant) | Withdraw your application |

**Status change triggers a notification** — when a recruiter accepts or rejects, the applicant gets a real-time `APPLICATION_ACCEPTED` or `APPLICATION_REJECTED` notification via WebSocket.

### Files

| File | Purpose |
|------|---------|
| `Application.java` | Entity — links user + job + status + cover letter |
| `ApplicationStatus.java` | Enum: `PENDING`, `REVIEWING`, `ACCEPTED`, `REJECTED` |
| `ApplicationRepository.java` | JPA queries including count by status (for dashboard) |
| `ApplicationService` / `ApplicationServiceImpl` | Business logic, prevents duplicate applications, company membership check for viewing |
| `ApplicationController.java` | REST controller |
| `ApplicationMapper.java` | Entity → DTO |
| `ApplicationRequest`, `UpdateApplicationStatusRequest`, `ApplicationResponse` | DTOs |

---

## Group 7 — Connections (Social Network)

### Endpoints (`/api/connections`)

| Method | Path | Access | What it does |
|--------|------|--------|--------------|
| `POST` | `/api/connections/request` | Auth | Send a connection request to another user |
| `PUT` | `/api/connections/{id}/accept` | Auth (receiver) | Accept a pending request → triggers `CONNECTION_ACCEPTED` notification |
| `PUT` | `/api/connections/{id}/reject` | Auth (receiver) | Reject a pending request |
| `DELETE` | `/api/connections/{id}` | Auth (either party) | Remove a connection or cancel a pending request |
| `GET` | `/api/connections/my` | Auth | Get all your accepted connections |
| `GET` | `/api/connections/pending` | Auth | Get received requests waiting for your response |
| `GET` | `/api/connections/sent-pending` | Auth | Get requests you sent that haven't been accepted yet |
| `GET` | `/api/connections/status/{userId}` | Auth | Check connection status with a specific user |

### Files

| File | Purpose |
|------|---------|
| `Connection.java` | Entity — requester, receiver, status |
| `ConnectionStatus.java` | Enum: `PENDING`, `ACCEPTED`, `REJECTED` |
| `ConnectionRepository.java` | JPA with queries to find connections by either party |
| `ConnectionService` / `ConnectionServiceImpl` | Business logic — prevents duplicate requests, checks party ownership before accept/reject |
| `ConnectionController.java` | REST controller |
| `ConnectionMapper.java` | Entity → DTO |
| `ConnectionRequest`, `ConnectionResponse`, `ConnectedUserResponse` | DTOs |

---

## Group 8 — Real-time Messaging

### Endpoints

**REST (`/api/conversations`, `/api/messages`):**

| Method | Path | Access | What it does |
|--------|------|--------|--------------|
| `GET` | `/api/conversations/my` | Auth | List all your conversations |
| `POST` | `/api/conversations/find-or-create` | Auth | Open or create a direct conversation with another user |
| `POST` | `/api/messages` | Auth | Send a message (REST fallback) |
| `GET` | `/api/messages/{conversationId}` | Auth (participant only) | Load message history (paginated) |
| `PUT` | `/api/messages/read/{conversationId}` | Auth (participant only) | Mark all messages in a conversation as read |

**WebSocket (STOMP):**

| Destination | Direction | What it does |
|-------------|-----------|--------------|
| `CONNECT /ws` (SockJS) | Client → Server | Establish WebSocket; JWT validated in STOMP header |
| `/app/chat.send` | Client → Server | Send a real-time message |
| `/topic/conversations/{id}` | Server → All participants | Broadcast new `MessageResponse` to the conversation room |
| `/user/queue/notifications` | Server → Specific user | Push new `NotificationResponse` |
| `/user/queue/notifications/count` | Server → Specific user | Push updated unread counts |

### Files

| File | Purpose |
|------|---------|
| `Conversation.java` | Entity — represents a chat thread |
| `ConversationParticipant.java` | Junction entity — maps users to conversations; enables group chat in future |
| `Message.java` | Entity — text content, sender reference, optional `Job` reference (for job-related messages), read flag |
| `ConversationRepository`, `ConversationParticipantRepository`, `MessageRepository` | JPA data access |
| `ConversationService` / `ConversationServiceImpl` | Find-or-create logic — prevents duplicate 1-on-1 conversations |
| `MessageService` / `MessageServiceImpl` | Saves message, broadcasts to `/topic/conversations/{id}` via `SimpMessagingTemplate`, fires notification to other participants |
| `ConversationController`, `MessageController` | REST controllers |
| `WebSocketController.java` | STOMP `@MessageMapping("/chat.send")` handler — validates auth and delegates to `MessageService` |
| `WebSocketConfig.java` | Configures the STOMP broker (`/topic`, `/queue`), endpoint `/ws` with SockJS, and heartbeat scheduler |
| `WebSocketSecurityConfig.java` | Intercepts and validates JWT on STOMP `CONNECT` frames — rejects unauthenticated connections |
| `MessageMapper.java` | Entity → DTO |
| `MessageRequest`, `MessageResponse`, `ConversationRequest`, `ConversationResponse` | DTOs |

---

## Group 9 — Notifications

### Endpoints (`/api/notifications`)

| Method | Path | Access | What it does |
|--------|------|--------|--------------|
| `GET` | `/api/notifications/my` | Auth | Paginated list of all your notifications |
| `GET` | `/api/notifications/unread-count` | Auth | Total unread count |
| `GET` | `/api/notifications/unread-counts` | Auth | Split count: `notificationCount` (bell) + `messageCount` (chat) |
| `PUT` | `/api/notifications/{id}/read` | Auth | Mark one notification as read |
| `PUT` | `/api/notifications/read-all` | Auth | Mark all bell notifications as read (excludes message type) |
| `PUT` | `/api/notifications/read-messages` | Auth | Mark all `MESSAGE` type notifications as read |

**Real-time delivery:** `NotificationService.createNotification()` calls `messagingTemplate.convertAndSendToUser(email, "/queue/notifications", ...)` — this pushes directly to the specific connected user.

**Notification types:**

| Type | Triggered by |
|------|-------------|
| `MESSAGE` | Someone sends you a message |
| `CONNECTION_REQUEST` | Someone sends you a connection request |
| `CONNECTION_ACCEPTED` | Your connection request was accepted |
| `JOB_APPLICATION` | Someone applies to your company's job |
| `APPLICATION_ACCEPTED` | Recruiter accepted your application |
| `APPLICATION_REJECTED` | Recruiter rejected your application |

### Files

| File | Purpose |
|------|---------|
| `Notification.java` | Entity — recipient user, type, entityId (what triggered it), content string, read flag |
| `NotificationType.java` | Enum of all 6 notification types |
| `NotificationRepository.java` | Queries: find by user, count unread, mark as read by type |
| `NotificationService` / `NotificationServiceImpl` | Creates and persists notification + immediately pushes via WebSocket |
| `NotificationController.java` | REST controller |
| `NotificationMapper.java` | Entity → DTO |
| `NotificationResponse`, `NotificationCountResponse` | DTOs (the count response splits notifications vs messages) |

---

## Group 10 — User Blocking

### Endpoints (`/api/blocks`)

| Method | Path | Access | What it does |
|--------|------|--------|--------------|
| `POST` | `/api/blocks/{blockedId}` | Auth | Block a user |
| `DELETE` | `/api/blocks/{blockedId}` | Auth | Unblock a user |
| `GET` | `/api/blocks/my` | Auth | List all users you have blocked |

**Block has downstream effects:** user search and network suggestions both exclude blocked users.

### Files

| File | Purpose |
|------|---------|
| `UserBlock.java` | Entity — blocker + blocked (two user references) |
| `UserBlockRepository.java` | Queries to check if a block exists |
| `UserBlockService` / `UserBlockServiceImpl` | Block/unblock logic with duplicate guard |
| `UserBlockController.java` | REST controller |

---

## Group 11 — Recruiter Dashboard

### Endpoints (`/api/recruiter`)

| Method | Path | Access | What it does |
|--------|------|--------|--------------|
| `GET` | `/api/recruiter/stats/company/{companyId}` | Auth (company member) | Aggregated stats: total jobs, open jobs, total/pending/accepted/rejected applications; per-job breakdown |

### Files

| File | Purpose |
|------|---------|
| `RecruiterDashboardService` / `RecruiterDashboardServiceImpl` | Aggregates counts from `JobRepository` and `ApplicationRepository`; verifies caller is a company member |
| `RecruiterDashboardController.java` | REST controller |
| `RecruiterDashboardResponse.java` | Nested DTO with company summary + per-job stats list |

---

## Group 12 — Infrastructure (Config, DB, Exceptions, Mappers)

### Configuration

| File | Purpose |
|------|---------|
| `application.yml` | Main config — DB, Liquibase, OAuth2, JWT, file storage, logging, WebSocket |
| `application.properties` | ⚠️ **Contains only** `spring.application.name=V0` — this is redundant; the real app name is set in `.yml`. This file is a leftover from Spring Initializr and can be cleaned up |
| `OpenApiConfig.java` | Configures Swagger UI with JWT `bearerAuth` security scheme so you can test protected endpoints directly from the browser |
| `WebConfig.java` | Maps `/uploads/**` URL path to the filesystem folder so uploaded files are publicly accessible |
| `docker-compose.yml` / `Dockerfile` | Container setup for deployment |

### Database Migrations (Liquibase)

| Migration | What it creates |
|-----------|----------------|
| `V1` | `users` table |
| `V2` | `profiles` table |
| `V3` | `skills` |
| `V4` | `educations` |
| `V5` | `experiences` |
| `V6` | `companies` |
| `V7` | `company_users` |
| `V8` | `jobs` |
| `V9` | `applications` |
| `V10` | `connections` |
| `V11` | `user_blocks` |
| `V12` | `conversations` |
| `V13` | `conversation_participants` |
| `V14` | `messages` |
| `V15` | `notifications` |
| `V16–V20` | Column fixes, added `updated_at`, `provider_id`, port fixes, company `domain` column |

### Exception Handling

| File | Purpose |
|------|---------|
| `GlobalExceptionHandler.java` | `@RestControllerAdvice` — catches all custom and Spring exceptions globally; returns consistent `ErrorResponse` JSON |
| `ErrorResponse.java` | Standard error shape: `timestamp`, `status`, `error`, `message`, `validationErrors` |
| `ResourceNotFoundException` | 404 — entity not found |
| `UnauthorizedException` | 403 — you don't have permission |
| `DuplicateResourceException` | 409 — already exists |
| `BadRequestException` | 400 — invalid input at business logic level |

### Mappers

All mappers follow one pattern — a static `toResponse(entity)` method. None use MapStruct (manual mapping). One mapper per domain.

| Mapper | Converts |
|--------|---------|
| `UserMapper` | `User` → `UserResponse` |
| `ProfileMapper` | `Profile` → `ProfileResponse` |
| `SkillMapper` | `Skill` → `SkillResponse` |
| `EducationMapper` | `Education` → `EducationResponse` |
| `ExperienceMapper` | `Experience` → `ExperienceResponse` |
| `CompanyMapper` | `Company` → `CompanyResponse` |
| `JobMapper` | `Job` → `JobResponse` |
| `ApplicationMapper` | `Application` → `ApplicationResponse` |
| `ConnectionMapper` | `Connection` → `ConnectionResponse` |
| `MessageMapper` | `Message` → `MessageResponse` |
| `NotificationMapper` | `Notification` → `NotificationResponse` |

---

## Issues Found

| # | Severity | File | Issue |
|---|----------|------|-------|
| 1 | Low | `JobController.java` | **Unused imports** — `UserRepository` and `ResourceNotFoundException` are imported but there is no field for `UserRepository` in the controller, and `ResourceNotFoundException` is never thrown directly there. Both are dead imports. |
| 2 | Low | `src/main/resources/application.properties` | **Redundant file** — contains only `spring.application.name=V0`. The real config is entirely in `application.yml`. This conflicts with the name set there (`JobStream`). The `.properties` file can be deleted or left empty. |
| 3 | Low | `CompanyRole.java` | **Single-value enum** — only `OWNER` is defined. If you plan to add `ADMIN`, `HR`, `RECRUITER` roles per company, the infrastructure is in place but the enum needs expanding. If no plans exist, this is just over-engineering. |
| 4 | Note | `WebSocketController.java` | The `@MessageMapping("/chat.send")` handler calls `messageService.sendMessage()` which broadcasts to `/topic/conversations/{id}`. However the return value of the service call (`MessageResponse response`) is stored locally but **not used** — the broadcast already happens inside the service via `SimpMessagingTemplate`. The local variable is dead code, though harmless. |
| 5 | Note | `SecurityConfig.java` | CORS is configured with `allowedOriginPatterns("*")` + `allowCredentials(true)`. This is fine in development but in production you should restrict it to your actual frontend origin. |
