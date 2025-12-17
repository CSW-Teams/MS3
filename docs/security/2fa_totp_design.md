# TOTP 2FA High-Level Design for MS3

## Current Authentication Overview
- **Endpoint & Flow**: `/login/` REST endpoint authenticates email/password, applies Cloudflare Turnstile when client IP/email is blacklisted, and issues JWT on success.【F:src/main/java/org/cswteams/ms3/rest/LoginRestEndpoint.java†L27-L117】
- **Security Configuration**: Spring Security permits `/login/`, requires authentication elsewhere, runs stateless JWT sessions, and injects a JWT validation filter before username/password processing.【F:src/main/java/org/cswteams/ms3/security/SecurityConfigurer.java†L41-L76】
- **JWT Handling**: `JwtUtil` creates HS256 tokens embedding roles and tenant, with 1-hour expiry; validation ensures subject match and expiry check.【F:src/main/java/org/cswteams/ms3/utils/JwtUtil.java†L33-L87】
- **Request Filtering**: `JwtRequestFilters` extracts Bearer token, loads user via `LoginController`, validates JWT, sets `SecurityContext`, and resolves tenant context from the token; defaults to `public` when no token is present.【F:src/main/java/org/cswteams/ms3/filters/JwtRequestFilters.java†L35-L88】
- **User Identity & Persistence**: `SystemUser` stores personal data, email, bcrypt-hashed password, roles (`SystemActor`), and tenant; loaded via `SystemUserDAO` by email or email/password.【F:src/main/java/org/cswteams/ms3/entity/SystemUser.java†L24-L99】【F:src/main/java/org/cswteams/ms3/dao/SystemUserDAO.java†L1-L12】
- **Frontend Login UX**: React `LoginView` posts credentials (and optional Turnstile token) through `LoginAPI` to `/api/login/`, saves JWT/user metadata in `localStorage`, and prompts for role selection when multiple actors exist.【F:frontend/src/views/utente/LoginView.js†L114-L197】【F:frontend/src/API/LoginAPI.js†L9-L24】
- **Additional Security Controls**: `BlacklistService` tracks repeated failures per IP/email (Caffeine cache) to require captcha; Turnstile token validation uses remote API.【F:src/main/java/org/cswteams/ms3/security/BlacklistService.java†L11-L33】【F:src/main/java/org/cswteams/ms3/utils/TurnstileService.java†L11-L49】

## 2FA Integration Options
### Enrollment Flow
- **Option A: User-initiated opt-in**
  - Authenticated users request enrollment; backend generates TOTP secret, returns provisioning URI/QR.
  - User confirms by submitting initial OTP before marking 2FA active.
- **Option B: Admin-driven enrollment**
  - Admin endpoint triggers secret generation for target user; user completes confirmation.
- **Option C: On-first-login prompt when policy requires**
  - After primary authentication, backend flags requirement; frontend guides immediate enrollment before granting full session.

### Login Flow
- **Option A: Two-step challenge**
  - Existing `/login/` validates credentials; on 2FA-enabled accounts responds with “OTP required” status plus temporary transaction token (non-JWT) to validate OTP in a follow-up call.
- **Option B: Single combined endpoint**
  - `/login/` accepts optional OTP; if missing for enabled users, responds with challenge indicator; when present and valid, issues JWT.
- **Option C: Dedicated OTP endpoint with short-lived partial session**
  - Credentials endpoint returns short-lived signed token limited to OTP verification; OTP endpoint swaps it for full JWT.

### Disablement & Recovery
- **Option A: Self-service disable with re-authentication + OTP confirmation**.
- **Option B: Admin override endpoint to disable for a user (audited)**.
- **Option C: Backup codes** (optional): generate hashed one-time codes during enrollment; usable when device unavailable; download-only display with regeneration invalidating old codes.

### Enforcement Models
- **Optional per user** (default off, user chooses to enable).
- **Role-based** (e.g., required for admins/planners while optional for doctors).
- **Global enforcement** via configuration flag or tenant-level policy; could block login completion until enrolled.

## Data Model & Persistence Impact
- Extend `SystemUser` (or linked 2FA entity) with fields: `totpSecret` (encrypted/encoded), `twoFactorEnabled` flag, `enrollmentConfirmedAt`, `lastVerifiedAt`, optional `backupCodes` (hashed list + `generatedAt`).
- Store secrets encrypted at rest (e.g., JCE + environment key) or encoded with HMAC key derivation; never log secrets or codes.
- Record failed OTP attempts and lockout timestamps to mitigate brute force; could reuse `BlacklistService` with OTP-specific keys.
- Database migration: add columns to `ms3_system_user` (or new table for 2FA settings) and table for backup codes if chosen.

## Backend API Design (Conceptual)
- **Enrollment**
  - `POST /2fa/enroll`: authenticated user (or admin specifying user) triggers secret generation; returns provisioning URI/QR data and masked recovery code list if enabled.
  - `POST /2fa/confirm`: accepts OTP (and optionally backup code) to finalize activation; persists `twoFactorEnabled` and `enrollmentConfirmedAt`.
- **Login / Challenge**
  - `POST /login/`: current credentials step; if 2FA required and OTP missing/invalid, respond with 401/403 plus challenge token and hint (e.g., `requires2fa=true`).
  - `POST /login/otp` (or `/2fa/verify`): accepts challenge token + OTP; issues normal JWT on success.
- **Disable**
  - `POST /2fa/disable`: requires primary auth + OTP (or admin override) to clear secret/flags; optionally rotate backup codes.
- **Error Handling & Security**
  - Rate-limit OTP attempts per user/IP (reuse blacklist + counters), exponential backoff or temporary lockout.
  - Allow limited clock skew (e.g., ±1 time step) configurable; use TOTP library supporting drift windows.
  - Log/audit enrollment, confirmation, disablement, failed OTP, backup code use; align with existing logging conventions in login flow.

## Frontend Flow Impact
- **Login UX**: After credential submit, handle “2FA required” response by showing OTP input modal; include retry handling and error feedback before storing JWT. Persist temporary challenge token client-side (memory) rather than `localStorage`.
- **Enrollment UI**: Add protected page/modal to display QR code and manual key, accept OTP for confirmation, show/download backup codes, and surface enforcement prompts when required.
- **Profile Settings**: Surface 2FA status, enable/disable toggle with OTP confirmation, and backup code regeneration.
- **State Management/API**: Extend `LoginAPI` and related hooks to handle challenge token and new endpoints; update role-selection flow to wait for full JWT issuance.

## Testing Strategy (Design-Level)
- **Backend**: Unit tests for TOTP verification (valid/invalid, drift, reuse), secret generation, backup code hashing; integration tests for login + challenge flow, enrollment/disable endpoints, blacklist interactions, tenant propagation with JWT issuance.
- **Frontend**: Component tests for OTP prompt, enrollment QR display, backup code rendering; integration tests for login flow with mocked API responses; ensure localStorage only set after successful OTP.
- **Security Regression**: Tests for brute-force throttling, expired challenge tokens, replayed backup codes, and logging/auditing where applicable.

## Implementation Approaches Comparison
- **Approach 1: Credential-first then OTP challenge (two-step)**
  - *Pros*: Minimal change to existing `/login/` request; clear separation of password vs OTP validation; easier to rate-limit OTP step.
  - *Cons*: Requires new challenge token mechanism and state handling; more round trips.
  - *Impacted areas*: `LoginRestEndpoint`, JWT issuance logic, new 2FA controller/service, `LoginView` OTP UI, `LoginAPI` expansions, security config for new endpoints.
- **Approach 2: Single `/login/` with conditional OTP field**
  - *Pros*: Keeps one endpoint; simpler client flow.
  - *Cons*: Harder to signal partial success; password and OTP validation coupled; risk of leaking timing info.
  - *Impacted areas*: `LoginRestEndpoint` branching, `JwtRequestFilters` unaffected, frontend login form to optionally collect OTP when prompted.
- **Approach 3: Spring Security multi-auth filter/provider chain**
  - *Pros*: Uses authentication mechanisms (custom `AuthenticationProvider` for TOTP), centralized security context.
  - *Cons*: Higher complexity; requires deeper security config changes from current `WebSecurityConfigurerAdapter` patterns.
  - *Impacted areas*: `SecurityConfigurer` overhaul, new filters/providers, dedicated service beans, frontend similar to Approach 1.

## Open Questions / Decisions Needed
- Should 2FA be optional, role-based, or globally enforced? How to treat existing users on rollout?
- Where to store secrets (DB column encryption vs external vault)? Key management expectations?
- Are backup codes required? If yes, how many and how are they delivered to users?
- Desired UX for challenge token lifetime and maximum OTP retries before lockout?
- Should admin overrides bypass OTP or require delegated approval?

