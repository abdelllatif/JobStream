# Cahier des Charges - JobStream Frontend (Angular 20 Standalone)

## 1. Project Overview

JobStream is a premium professional networking platform. The frontend is built with **Angular 20** using a **Standalone architecture**, targeting high performance, SEO optimization, and a LinkedIn-inspired UX.

## 2. Technical Architecture & Design Patterns

### 2.1 Angular 20 Standalone

- **Zero NgModules**: All components, directives, and pipes are `standalone: true`.
- **Signals**: Primary state management for reactive data flows (replacing complex RxJS logic where possible).
- **Control Flow**: Optimized `@if`, `@for`, and `@switch` syntax.

### 2.2 Application Structure

- **Core Module (`src/app/core`)**: Singleton services, global guards, and interceptors.
- **Shared Module (`src/app/shared`)**: Reusable UI components (buttons, cards, modals), common pipes, and interfaces.
- **Features (`src/app/features`)**: Domain-specific pages (Lazy Loaded).
  - `auth/`: Login, Register, OAuth2 Callback.
  - `job-feed/`: Infinite scroll jobs, search bar.
  - `network/`: User discovery, connection requests.
  - `profile/`: User resume, experiences, skills.
  - `messages/`: Chat interface, real-time sync.

## 3. Authentication & Token Lifecycle

### 3.1 Social Login (Google OAuth2)

- **Flow**:
  1. Frontend navigates to `http://localhost:8081/api/auth/google` (This is an explicit redirect endpoint that points to the internal Spring Security OAuth2 flow).
  2. Google authenticates and redirects to Backend.
  3. Backend redirects back to Frontend (`/oauth2/callback?token=...&refreshToken=...`).
  4. `OAuth2CallbackComponent` extracts tokens and stores them.
- **Lazy Registration**: No separate "Register via Google" page is needed. If a user logs in via Google for the first time, the system automatically creates their account, extracts their names, and initializes an empty profile (matching the local registration flow).
- **Backend Sync**: Profiles are automatically linked via the `sub` (Google ID).

### 3.2 Security Strategy

- **Token Storage**: Store only `accessToken` and `refreshToken` in browser storage.
- **Interceptors**:
  - `AuthInterceptor`: Injects Bearer token into every request.
  - `ErrorInterceptor`: Catches `401` errors and triggers the `RefreshService`.
- **Token Validation**: The `AuthGuard` checks token expiration locally before allowing navigation. If expired, it triggers a refresh.

## 4. Workflows & Detailed Page Specs

### 4.1 Home (The "Dynamic" Landing Page)

- **Sections (One-Page feel)**:
  - **Hero**: Catchy value proposition + search preview.
  - **About**: Platform benefits.
  - **FAQ**: Common questions.
  - **Contact**: Minimalistic contact form.
- **Navigation**:
  - Unauthenticated: Navbar links scroll to sections via `@angular/router` anchor scrolling.
  - Authenticated: Automatic redirect to `/job-feed`.

### 4.2 Job Feed (Discovery Engine)

- **Search Bar**:
  - Keywords, Location, Category.
  - **Instant Search**: RxJS `debounceTime(300)` and `distinctUntilChanged` on inputs.
- **Job Cards**: Company logo, title, snippets, and "Quick Save" heart icon.

### 4.3 Network (Professional Connections)

- **Logic**: Shows all users except current user and admins.
- **Statuses**: `NONE`, `PENDING_SENDER`, `PENDING_RECEIVER`, `ACCEPTED`, `REJECTED`, `BLOCKED`.
- **Actions**: Dynamic buttons based on status (e.g., "Accept", "Cancel Request", "Connect").

### 4.4 Messages & Real-time Sync

- **Messaging**: Split-view layout (Master: Conversations, Detail: Active Thread).
- **Protocols**: WebSocket (`/ws`) for instant message delivery and typing indicators.

### 4.5 Profile Management

- **Sections**: Education, Experience, Skills, Profile Photo, and CV (PDF preview/download).
- **Edit Controls**: Visible only to the owner via `ProfileService.isOwner(ownerId)`.

### 4.6 Company Dashboard

- **Role-based Access**: Accessible only to users with `ROLE_RECRUITER` or `ROLE_ADMIN`.
- **Operations**:
  - Post/Edit Jobs.
  - View Applicants list with status badges (`APPLIED`, `INTERVIEWING`, `HIRED`, `REJECTED`).

## 5. Premium UX & Performance

- **Sidebar (Auth-Only)**: Desktop persistent navigation / Mobile drawer.
  - Dynamic counts for Messages and Notifications.
  - Badge logic: Disappear when clicking the target page; stay hidden until new data arrives via WS.
- **Responsive Layout**: TailwindCSS (or Vanilla CSS Grid/Flexbox) for "Phone First" experience.
- **Loading States**: Shimmer effect (Skeleton screens) for job cards and user lists.
