# TOTP 2FA High-Level Design for MS3

## Current Authentication Overview
- **Endpoint & Flow**: `/login/` REST endpoint authenticates email/password, applies Cloudflare Turnstile when client IP/email is blacklisted, and issues JWT on success.【F:src/main/java/org/cswteams/ms3/rest/LoginRestEndpoint.java†L27-L117】
- **Security Configuration**: Spring Security permits `/login/`, requires authentication elsewhere, runs stateless JWT sessions, and injects a JWT validation filter before username/password processing.【F:src/main/java/org/cswteams/ms3/security/SecurityConfigurer.java†L41-L76】
- **JWT Handling**: `JwtUtil` creates HS256 tokens embedding roles and tenant, with 1-hour expiry; validation ensures subject match and expiry check.【F:src/main/java/org/cswteams/ms3/utils/JwtUtil.java†L33-L87】
- **Request Filtering**: `JwtRequestFilters` extracts Bearer token, loads user via `LoginController`, validates JWT, sets `SecurityContext`, and resolves tenant context from the token; defaults to `public` when no token is present.【F:src/main/java/org/cswteams/ms3/filters/JwtRequestFilters.java†L35-L88】
- **User Identity & Persistence**: `SystemUser` stores personal data, email, bcrypt-hashed password, roles (`SystemActor`), and tenant; loaded via `SystemUserDAO` by email or email/password.【F:src/main/java/org/cswteams/ms3/entity/SystemUser.java†L24-L99】【F:src/main/java/org/cswteams/ms3/dao/SystemUserDAO.java†L1-L12】
- **Frontend Login UX**: React `LoginView` posts credentials (and optional Turnstile token) through `LoginAPI` to `/api/login/`, saves JWT/user metadata in `localStorage`, and prompts for role selection when multiple actors exist.【F:frontend/src/views/utente/LoginView.js†L114-L197】【F:frontend/src/API/LoginAPI.js†L9-L24】
- **Additional Security Controls**: `BlacklistService` tracks repeated failures per IP/email (Caffeine cache) to require captcha; Turnstile token validation uses remote API.【F:src/main/java/org/cswteams/ms3/security/BlacklistService.java†L11-L33】【F:src/main/java/org/cswteams/ms3/utils/TurnstileService.java†L11-L49】

## Decided Policies
- **Enrollment**: User-initiated opt-in; enrollment must be confirmed with an OTP before activation.
- **Login**: Two-step challenge. Password validation happens first; when 2FA is required, `/login/` returns `requires2fa=true` and a user-bound challenge marker (non-JWT). OTP verification occurs on a dedicated endpoint.
- **Disablement & Recovery**: Self-service disable is available; recovery codes are supported.
- **Enforcement**: Optional by default with role-based enforcement for designated roles. No pre-password leakage of enforcement state.
- **Secret Storage**: TOTP and recovery secrets are derived deterministically using an HMAC master key; no per-user TOTP secret is persisted.
- **Lockout/Throttling**: Attempt-count lockout replaces blacklist TTL. After N failed OTP verifications (configurable), all OTP verification attempts are blocked for 60 seconds. Counter resets on success.
- **Challenge TTL**: No time-based expiry for the challenge token. Security relies on OTP freshness (30s step), user-bound challenge markers, and the lockout policy to prevent brute-force or replay.
- **Recovery Code Semantics**: Recovery codes are deterministically derived; only `lastRecoveryCodeIdUsed` is stored. Codes support jump-ahead semantics and the final code disables 2FA and forces re-enrollment/rotation.

## Configuration & Deployment
- **Environment variables** (must be present before enabling 2FA in any environment):
  - `HMAC_MASTER_KEY`: server-wide secret used for deterministic TOTP/recovery derivation; supply via secret manager/injection, never commit or log.
  - `MAX_OTP_ATTEMPTS`: consecutive OTP failures allowed before lockout is enforced.
  - `OTP_LOCKOUT_SECONDS`: lockout window applied after exceeding `MAX_OTP_ATTEMPTS`.
  - `ENFORCED_2FA_ROLES`: comma-separated roles that require 2FA after password verification; enforcement is not leaked before password success.
  - `RECOVERY_CODE_COUNT`: total recovery codes per enrollment; the final code disables 2FA and rotates the derivation salt/version.
- **Deployment checks**:
  - Validate that `HMAC_MASTER_KEY` is injected securely in CI/CD (sealed secrets/secret manager) and never echoed in build logs.
  - Ensure the configured lockout values align with UX messaging (seconds remaining) and that role enforcement matches RBAC policy.
  - Coordinate environment updates so backend and frontend agree on lockout messaging and recovery code count.

## Data Model & Persistence Impact
- Extend `SystemUser` (or a linked 2FA entity) with fields: `twoFaVersionOrSalt` (HMAC salt/version), `twoFactorEnabled`, `enrollmentConfirmedAt`, `lastRecoveryCodeIdUsed`, and lockout counters/timestamps consistent with the attempt-count policy.
- Store only derivation inputs (not the TOTP secret itself). Secrets and recovery codes are derived from `HMAC(masterKey, userStableId || ":" || twoFaVersionOrSalt || ":totp")` and `HMAC(masterKey, userStableId || ":" || twoFaVersionOrSalt || ":recovery:" || i)` respectively. Never log secrets or codes.
- Record failed OTP attempts and lockout timestamps in a cache or DB structure consistent with existing `BlacklistService` patterns to enforce the 60-second lockout window.
- Database migration: add the new columns/fields above to `ms3_system_user` (or the chosen 2FA table). No column for storing raw TOTP secrets is needed.

## Backend API Design (Conceptual)
- **Enrollment**
  - `POST /2fa/enroll`: authenticated user triggers deterministic secret derivation and receives provisioning URI/QR plus recovery code list (generated on-the-fly). Uses `twoFaVersionOrSalt` persisted per user.
  - `POST /2fa/confirm`: accepts OTP to finalize activation; sets `twoFactorEnabled` and `enrollmentConfirmedAt`.
- **Login / Challenge**
  - `POST /login/`: validates credentials (and Turnstile when blacklisted). If 2FA is required, returns `requires2fa=true` and a user-bound challenge marker; JWT is **not** issued yet.
  - `POST /login/otp` (or `/2fa/verify`): accepts challenge marker + OTP/recovery code; issues normal JWT on success and resets OTP failure counters.
- **Disable**
  - `POST /2fa/disable`: requires primary auth + OTP or recovery code. Final recovery code (id == N) also disables 2FA, rotates `twoFaVersionOrSalt`, and requires re-enrollment.
- **Error Handling & Security**
  - Enforce attempt-count lockout (N failures → 60s block) server-side, keyed consistently with existing blacklist/tenant handling. No challenge TTL; prevent OTP replay by binding challenge to user and invalidating on JWT issuance or lockout.
  - Allow minimal clock skew consistent with the TOTP library. Never log secrets, codes, or raw HMAC material. Provide safe, generic error messages.
  - Log/audit enrollment, confirmation, disablement, failed OTP, recovery code use, and lockout triggers.

## Frontend Flow Impact
- **Login UX**: After credential submit, handle “2FA required” by showing an OTP/recovery input modal. Store the challenge marker only in memory. JWT/localStorage is updated **only after** successful OTP verification. Show lockout messaging (including wait time if provided).
- **Enrollment UI**: Provide QR/manual key, capture OTP for confirmation, display recovery codes, and explain jump-ahead + final-code-disable semantics. Surface enforcement prompts when user roles require 2FA.
- **Profile Settings**: Show 2FA status, enable/disable paths, recovery code regeneration (via version rotation), and lockout status if relevant.
- **State Management/API**: Extend `LoginAPI` and related hooks to handle the challenge marker, OTP verification endpoint, and recovery code usage. Ensure role-selection waits for the full JWT after OTP success.

## Testing Strategy (Design-Level)
- **Backend**: Unit tests for deterministic HMAC derivation, TOTP verification (valid/invalid, skew), lockout counters, jump-ahead recovery code validation, and disable-on-final-code behavior. Integration tests for two-step login, challenge binding, lockout across challenges, enrollment/disable flows, and tenant propagation in JWT issuance.
- **Frontend**: Component tests for OTP modal, lockout messaging, enrollment QR display, recovery code rendering, and copy explaining jump-ahead/final-code disablement. Integration tests for login flow ensuring JWT is set only after OTP success and lockout responses are surfaced.
- **Security Regression**: Tests for brute-force throttling, replayed OTP/recovery code attempts, absence of challenge TTL bypass, and logging/auditing hooks.

## Operational Runbook
- **Lockout handling**: After `MAX_OTP_ATTEMPTS` failures, block OTP/recovery verification for `OTP_LOCKOUT_SECONDS`. Communicate remaining lockout duration to the user; counters reset on a successful verification. Lockout applies even if a new challenge marker is requested, since no challenge TTL is applied.
- **Recovery codes**: Accept any recovery code with an id ≥ `lastRecoveryCodeIdUsed + 1`; update the stored id to the submitted code’s id (jump-ahead). Reject codes with lower ids as replays.
- **Final code behavior**: When the submitted recovery code id equals `RECOVERY_CODE_COUNT`, disable 2FA, rotate `twoFaVersionOrSalt`, and require the user to re-enroll before 2FA is active again. Update UX to surface the need to re-enroll.
- **Challenge tokens**: Challenges do not expire by TTL. Replay protection relies on OTP freshness, user-bound challenge markers, and the lockout policy; backend should prevent multiple JWT issuances from the same challenge.
- **Re-enrollment/disablement**: Allow self-service disable via OTP or recovery code. On disablement or final-code usage, rotate the salt/version and provide new recovery codes during re-enrollment.
- **Secret hygiene**: Treat `HMAC_MASTER_KEY` as a production secret. Validate secret presence post-deploy and rotate via secret manager if compromised; never store it in application logs or persistent config files.


