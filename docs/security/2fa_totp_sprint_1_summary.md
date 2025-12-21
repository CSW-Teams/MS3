# TOTP 2FA Sprint 1 Summary

## Purpose and Scope
- Summarizes the agreed TOTP-based two-factor authentication (2FA) design and the Codex implementation prompts guiding Sprint 1.
- Covers backend, frontend, database, and operational expectations; emphasizes deterministic secret handling, role-based enforcement, and lockout policy.

## Key Design Decisions (from `docs/security/2fa_totp_design.md`)
- **Flow**: Password-first login; if 2FA is required/enabled, backend returns `requires2fa=true` and a user-bound challenge marker. JWT is issued only after OTP/recovery verification.
- **Enrollment**: User-initiated. Secrets and recovery codes are **derived deterministically** from an HMAC master key + per-user salt/version (`twoFaVersionOrSalt`); no secrets are persisted. Enrollment must be confirmed with an OTP before activation.
- **Recovery Codes**: Deterministically derived. Only `lastRecoveryCodeIdUsed` is stored. Jump-ahead semantics allow submitting any code ≥ expected; the final code disables 2FA and rotates the salt/version.
- **Lockout**: Attempt-count policy—after N failed OTP attempts (configurable), block verification for 60s; counters reset on success. No challenge TTL; security relies on OTP freshness + lockout + user-bound challenge.
- **Role Enforcement**: Config-driven list of roles that must complete 2FA after password verification. Enforcement is not disclosed before password success.
- **Backend Data Model**: Add columns to user tables: `twoFaVersionOrSalt`, `twoFactorEnabled`, `enrollmentConfirmedAt`, `lastRecoveryCodeIdUsed`, `otpFailedAttempts`, `otpLockoutEndsAt`, `otpLastAttemptAt`.
- **Frontend UX**: Two-step login with OTP/recovery modal, lockout messaging, and enrollment UI (QR/manual key + recovery codes). JWT is stored only after OTP success.
- **Operational**: Require env vars (`HMAC_MASTER_KEY`, `MAX_OTP_ATTEMPTS`, `OTP_LOCKOUT_SECONDS`, `ENFORCED_2FA_ROLES`, `RECOVERY_CODE_COUNT`). Keep secrets out of logs; align backend/frontend copy for lockout and recovery behavior.

## Codex Implementation Prompts Alignment (from `docs/security/2fa_totp_codex_implementation_prompts.md`)
1. **DB Schema for 2FA State & Lockout**: Add the 2FA columns above, idempotent migrations, and entity mappings with safe defaults for multi-tenant users.
2. **Deterministic HMAC Derivation Utilities**: Introduce config-bound master key and recovery count; derive TOTP/recovery codes via HMAC(userId + salt + purpose); no logging of secrets; add tests for determinism.
3. **Enrollment Endpoints & Recovery Exposure**: Authenticated `/2fa/enroll` to return provisioning data + one-time recovery codes; `/2fa/confirm` to activate 2FA after OTP validation; avoid persisting derived secrets.
4. **Login Step 1 (Credential + 2FA Challenge)**: Adjust `/login/` to return `requires2fa` + user-bound challenge without issuing JWT; maintain Turnstile/blacklist; no TTL on challenges, rely on OTP freshness + lockout.
5. **OTP Verification Endpoint**: Validate challenge + OTP/recovery, enforce attempt-count lockout, support jump-ahead recovery and final-code disable/rotation, then issue JWT; prevent replay.
6. **Role-Based Enforcement & Auditing**: Configurable required roles; block JWT until 2FA complete; log enrollment/disable, failures, lockout triggers, successes without sensitive data.
7. **Frontend Login OTP Modal & Lockout UX**: Handle `requires2fa` responses in memory, show OTP/recovery modal with lockout info, and persist JWT only after success; add tests.
8. **Frontend Enrollment & Recovery UX**: Provide enrollment page with QR/manual key, OTP confirmation, recovery-code display (one-time), and copy explaining jump-ahead/final-code disablement; add tests.
9. **Backend Tests for Lockout/Recovery**: Cover lockout entry/exit, jump-ahead acceptance, stale-code rejection, final-code disablement/rotation, and JWT gating for enforced roles using controllable clocks.
10. **Docs/Config & Runbook Updates**: Document env vars, lockout policy, recovery semantics, no-TTL challenge rationale, and deployment/ops guidance aligned with the design.
11. **Schema Initializer Integration**: Ensure the 2FA migration runs for public and all tenant schemas via `SchemasInitializer` (or inlined create scripts) with idempotent search_path handling.
12. **Docker/Local DB Bootstrap Alignment**: Compose/init scripts must provision roles/permissions so the initializer can apply 2FA columns; document env vars and volume resets for schema changes.
13. **Test/CI Bootstrap**: Tests/CI must run with the schema initializer (DDL off) so all schemas include 2FA columns; document disposable Postgres setup and profile usage.

## Definition of Done Highlights
- No stored TOTP secrets; only derivation inputs and counters persisted.
- JWT issued only after OTP/recovery success; role-based enforcement honored.
- Lockout enforced after N failures with 60s block; challenge has no TTL.
- Recovery codes respect jump-ahead semantics; final code disables 2FA and rotates salt/version.
- Docs and configs list required env vars and operational guidance; tests cover lockout/recovery behaviors and JWT gating.
