FRONTEND SPECIFICATION - JobStream (Angular 20 Standalone)

But: Ce document décrit l'architecture frontend recommandée, les pages, les services à implémenter, les interfaces TypeScript basées sur les DTOs backend, la stratégie d'authentification, les guards/interceptors, la configuration Tailwind CSS, et un mapping complet endpoints -> services.

1. Objectif

- Construire le frontend Angular 20 (standalone components) pour JobStream.
- Respecter la structure: `core/` (services, models, guards, interceptors), `features/` (pages/features), `shared/` (navbar, footer, UI atoms).
- Utiliser ReactiveFormsModule, Tailwind CSS et JWT pour l'auth.

2. Principes d'architecture

- Angular 20 standalone components (ng new --standalone)
- Core folder: centralise services, models (interfaces), guards, interceptors
- Features: Chaque fonctionnalité (jobs, companies, profile, messages, applications, premium) est un dossier standalone
- Shared: `navbar`, `footer`, composants UI réutilisables
- State: service-oriented + RxJS subjects (pas de store centralisé obligatoire pour MVP)

3. Structure recommandée

src/app/
  core/
    services/
      auth.service.ts
      job.service.ts
      company.service.ts
      profile.service.ts
      message.service.ts
      application.service.ts
      file.service.ts
      search.service.ts
      premium.service.ts
    models/
      job.model.ts
      company.model.ts
      user.model.ts
      candidate-profile.model.ts
      application.model.ts
      message.model.ts
      auth.model.ts
    guards/
      auth.guard.ts
      role.guard.ts
      profile-owner.guard.ts
    interceptors/
      auth.interceptor.ts
  features/
    feed/
      feed.page.ts (standalone)
    job-detail/
      job-detail.page.ts
    company/
      company-detail.page.ts
    profile/
      my-profile.page.ts
      profile-edit.page.ts
    auth/
      login.page.ts
      register.page.ts
    messages/
      conversations.page.ts
      chat.page.ts
    applications/
      applications.page.ts
    premium/
      premium.page.ts
    search/
      search.page.ts
  shared/
    navbar.component.ts
    footer.component.ts
    ui/
      button.component.ts
      card.component.ts

4. Pages & routes

- /auth/login (Login)
- /auth/register
- /jobs (Feed) - liste paginée
- /jobs/:id (Job details)
- /companies (liste)
- /companies/:id (Company page)
- /profile/me (My profile)
- /profile/:id (Public profile view)
- /applications (Mes candidatures)
- /messages (Conversations list)
- /messages/:partnerId (Chat with partner)
- /premium (Abonnement)
- /search (Form + results)
- /admin/* (Admin dashboard - réservé aux ADMIN)

5. Mapping endpoints -> services (extraits)

AuthService
- POST /api/auth/register -> UserCreateRequestDTO -> returns UserResponseDTO
- POST /api/auth/login -> LoginRequestDTO -> returns AuthResponseDTO (token)
- POST /api/auth/logout -> void/string

JobService
- GET /api/jobs -> JobResponseDTO[]
- GET /api/jobs/{id} -> JobResponseDTO
- POST /api/jobs -> JobCreateRequestDTO -> JobResponseDTO
- PUT /api/jobs/{id} -> JobUpdateRequestDTO -> JobResponseDTO
- DELETE /api/jobs/{id}

CompanyService
- GET /api/companies
- GET /api/companies/{id}
- POST /api/companies -> CompanyCreateRequestDTO
- PUT /api/companies/{id}
- DELETE /api/companies/{id}

CandidateProfileService
- GET /api/candidate-profiles
- GET /api/candidate-profiles/{id}
- POST /api/candidate-profiles
- PUT /api/candidate-profiles/{id}
- DELETE /api/candidate-profiles/{id}

MessageService
- POST /api/messages/send?senderId&receiverId&content&jobId -> returns Message entity
- GET /api/messages/conversation/{u1}/{u2}
- GET /api/messages/conversation/{u1}/{u2}/paginated?page&size
- GET /api/messages/user/{userId}
- GET /api/messages/unread/{userId}
- PUT /api/messages/read/{messageId}
- PUT /api/messages/read-conversation/{u1}/{u2}
- DELETE /api/messages/{messageId}
- DELETE /api/messages/conversation/{u1}/{u2}
- GET /api/messages/unread-count/{userId}
- GET /api/messages/partners/{userId}

ApplicationService
- POST /api/appllications (note: spelling in backend)
- GET /api/appllications/{id}
- GET /api/appllications
- PUT /api/appllications/{id}
- DELETE /api/appllications/{id}

FileService
- POST /api/files/upload (multipart) -> returns filePath
- POST /api/files/upload-cv?userId -> returns filePath
- POST /api/files/upload-profile-picture?userId
- POST /api/files/upload-company-logo?companyId
- GET /api/files/download?filePath
- GET /api/files/view?filePath
- DELETE /api/files/delete?filePath
- GET /api/files/exists?filePath

SearchService
- GET /api/search/jobs?keyword&location&contractType&domainIds&page&size
- GET /api/search/jobs/advanced?...lots of params
- GET /api/search/jobs/recommended/{userId}?limit
- GET /api/search/candidates?keyword&location&skills&page&size
- GET /api/search/recruiters?keyword&company&page&size
- GET /api/search/suggestions?query&type

PremiumService
- POST /api/premium/subscribe?userId&planType
- POST /api/premium/payment/approve/{paymentId}
- POST /api/premium/payment/execute/{paymentId}?payerId
- PUT /api/premium/update/{subscriptionId}?planType
- DELETE /api/premium/cancel/{subscriptionId}
- GET /api/premium/active/{userId}
- GET /api/premium/user/{userId}
- GET /api/premium/check/{userId}
- GET /api/premium/plans

6. Interfaces TypeScript (exemples)

// job.model.ts
export interface Job {
  id: number;
  title: string;
  description: string;
  location: string;
  contractType: string;
  postedAt: string; // ISO
  updatedAt?: string;
  active: boolean;
  companyId: number;
  domainId: number;
  tagIds?: number[];
}

// company.model.ts
export interface Company {
  id: number;
  name: string;
  description?: string;
  website?: string;
  logoUrl?: string;
  userId?: number;
}

// user.model.ts
export type Role = 'CANDIDATE' | 'RECRUITER' | 'ADMIN';
export interface User {
  id: number;
  firstName?: string;
  lastName?: string;
  email: string;
  role: Role;
  profilePicture?: string;
  premiumUser?: boolean;
}

// candidate-profile.model.ts
export interface CandidateProfile {
  id: number;
  userId: number;
  phone?: string;
  address?: string;
  summary?: string;
  cvUrl?: string;
}

// application.model.ts
export type ApplicationStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED';
export interface Application {
  id: number;
  candidateProfileId: number;
  jobId: number;
  status: ApplicationStatus;
  appliedAt: string;
}

// message.model.ts
export interface Message {
  id: number;
  senderId: number;
  receiverId: number;
  content: string;
  jobId?: number;
  createdAt: string;
  read: boolean;
}

// auth.model.ts
export interface AuthResponse {
  token: string;
  userId?: number;
  email?: string;
  role?: Role;
  firstName?: string;
  lastName?: string;
  profilePicture?: string;
  premiumUser?: boolean;
}

7. Auth strategy & Interceptor

- Stockage du token: localStorage.setItem('jwt', token)
- AuthService: login(credentials): POST /api/auth/login -> store token; logout(): remove token
- AuthInterceptor: ajoute Authorization header si token présent; gérer 401 -> redirect to /auth/login
- Guards: AuthGuard (isAuthenticated), RoleGuard (check role claim in token), ProfileOwnerGuard (compare userId claim avec resource owner)

8. Reactive Forms & validation

- Utiliser ReactiveFormsModule dans pages de création/modification (job create, profile edit, company create, application cover letter)
- Validation côté frontend alignée sur annotations DTO (ex: title max 150, password min 8)

9. Tailwind CSS config

- Installer Tailwind (tailwindcss@latest) + postcss
- Ajouter directives dans styles.css : @tailwind base; @tailwind components; @tailwind utilities;
- Configurer palette et tokens
- Utiliser classes utilitaires pour UI responsive

10. Tests recommandés

- Unit tests (Jest/Angular TestBed): AuthService, AuthInterceptor, JobService (http mock), RoleGuard
- e2e smoke (Playwright): login -> open /jobs -> job details -> apply

11. Postman collection
- Fournir `postman_collection_jobstream.json` (voir fichier séparé) avec tous les endpoints listés ci-dessus, exemples de body et en-têtes (Content-Type + Authorization)

12. Livrables & planning
- FRONTEND-SPECIFICATION.md (ce fichier)
- postman_collection_jobstream.json
- examples/ (auth.service.ts, auth.interceptor.ts, role.guard.ts)
- Estimation: 12-18 jours (1 dev) / 6-10 jours (2 devs) pour MVP complet.

Annexes: remarques pratiques
- Attention au endpoint mal orthographié du backend: `/api/appllications` (3 l) -> utiliser tel quel ou corriger backend
- Les endpoints sécurisés nécessitent rôles : vérifier via annotations `@PreAuthorize`
- Pour upload de fichiers, utiliser FormData + multipart/form-data

-- fin --

