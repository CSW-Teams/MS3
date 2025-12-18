# Execution Overview
- **Execution Order**: Follow prompts in the order listed below (1 → 10). Each prompt is independent but assumes completion of all prior prompts.
- **Dependencies**: Later prompts depend on models, endpoints, and UI contracts established in earlier prompts (e.g., lockout state from prompt 1 is required by prompts 5–6; deterministic derivation utilities from prompt 2 are used throughout).
- **Manual Steps**: Before running implementation prompts, configure the server-wide HMAC master key as an environment/config property consistent with MS3 patterns (e.g., Spring property binding). Do not commit the secret.
- **Decision Summary**: User-initiated enrollment; two-step login challenge; self-service disable + recovery codes; role-based enforcement (optional by default); deterministic HMAC-derived TOTP and recovery codes; jump-ahead recovery semantics with final-code disable/rotation; no challenge TTL (rely on OTP freshness + lockout); attempt-count lockout after N failures → 60-second block (server-enforced).
- **Policy Reference**: All prompts must be executed in alignment with `docs/security/2fa_totp_design.md`, which remains the source of truth for policy decisions (no challenge TTL, deterministic HMAC derivation, jump-ahead recovery semantics, and lockout rules).

## Codex Prompt — DB schema for 2FA state & lockout
**Context**
Add persistence fields needed for deterministic HMAC derivation, enrollment state, recovery tracking, and OTP lockout counters without storing per-user TOTP secrets.

**Files to Read**
- `src/main/java/org/cswteams/ms3/entity/SystemUser.java`
- `src/main/java/org/cswteams/ms3/dao/SystemUserDAO.java`
- Existing DB migration scripts under `src/main/resources/db/migration/`
- Any JPA converters/embeddables related to auth state (search around security/user entities)

**Objectives**
- Design DB changes to add: `twoFaVersionOrSalt` (HMAC salt/version), `twoFactorEnabled`, `enrollmentConfirmedAt`, `lastRecoveryCodeIdUsed`, and OTP lockout counters/timestamps consistent with the 60s lockout policy.
- Ensure fields align with deterministic secret derivation (no stored TOTP secret) and jump-ahead recovery semantics.
- Keep multi-tenant considerations aligned with current user/tenant modeling.

**Output Requirements**
- Update/create Flyway migration(s) to add the new columns.
- Update `SystemUser` (or dedicated 2FA entity) to map the new fields with sensible defaults (e.g., `lastRecoveryCodeIdUsed=0`).
- Adjust DAO/repositories if needed for new queries related to lockout or 2FA status.

**Constraints**
- Java 11 / Spring Boot 2.1.x / Hibernate JPA conventions already in use.
- Preserve existing data; migrations must be backward-compatible and idempotent for production rollout.
- Do not store TOTP secrets or recovery code material—only derivation inputs.
- Follow MS3 architectural boundaries; keep controllers thin.

**Human Intervention**
If production data backfill is required for new non-null columns, coordinate with DBAs.

**Definition of Done (DoD)**
- New columns added with nullability/defaults that prevent login breakage for existing users.
- Entity mappings compile and pass validation.
- No stored TOTP secrets; only derivation inputs and counters are persisted.

## Codex Prompt — Deterministic HMAC derivation utilities
**Context**
Implement deterministic secret derivation for TOTP and recovery codes using an HMAC master key with per-user salt/version, plus formatting helpers.

**Files to Read**
- `src/main/java/org/cswteams/ms3/utils/JwtUtil.java` (for property binding patterns)
- Existing config/property classes under `src/main/java/org/cswteams/ms3/config/`
- Any existing crypto/HMAC utilities in the repo
- `docs/security/2fa_totp_design.md` (for requirements)

**Objectives**
- Add a configuration property for the server-wide HMAC master key (env-driven, never logged) and for the total number of recovery codes (N).
- Choose a stable per-user identifier (e.g., DB primary key) for derivation; justify based on repository reality and tenant boundaries.
- Implement derivation methods:
  - `totp_secret = Base32( HMAC(masterKey, userStableId + ":" + twoFaVersionOrSalt + ":totp") )` compatible with the TOTP library.
  - `recovery_code_i = Format( HMAC(masterKey, userStableId + ":" + twoFaVersionOrSalt + ":recovery:" + i) )` for i=1..N.
- Provide helpers to format recovery codes for display and parse submissions safely.
- Ensure no secrets or codes are logged; errors are generic.

**Output Requirements**
- New utility/service class for HMAC-based derivation and formatting.
- Configuration properties (with defaults where safe) for master key placeholder and recovery code count.
- Unit tests covering derivation determinism and encoding compatibility.

**Constraints**
- Do not wrap imports in try/catch.
- No new external dependencies unless already available; prefer standard Java/commons/Guava already in the project.
- Keep secrets in memory only as needed; avoid persistence/logging.

**Human Intervention**
Ensure the master key is supplied in each environment before enabling 2FA features.

**Definition of Done (DoD)**
- Deterministic derivation verified by tests; same inputs yield same outputs.
- Secrets/codes are encoded in Base32/printable format compatible with TOTP apps.
- No secrets or recovery codes are logged; error messages are safe and non-specific.

## Codex Prompt — Enrollment endpoints & recovery code exposure
**Context**
Add user-initiated enrollment APIs that derive secrets deterministically, present provisioning data, and finalize activation after OTP confirmation.

**Files to Read**
- `src/main/java/org/cswteams/ms3/rest/` controllers related to user settings
- `src/main/java/org/cswteams/ms3/service/` auth/user services
- `src/main/java/org/cswteams/ms3/security/SecurityConfigurer.java`
- `docs/security/2fa_totp_design.md`

**Objectives**
- Implement `POST /2fa/enroll` (authenticated) that generates provisioning URI/QR data using the derived TOTP secret and returns recovery codes (generated on-the-fly) without persisting them.
- Implement `POST /2fa/confirm` to validate an OTP against the derived secret and set `twoFactorEnabled/enrollmentConfirmedAt`; maintain `twoFaVersionOrSalt` for rotation.
- Ensure responses never return raw HMAC inputs; recovery codes are displayed only once per enrollment.

**Output Requirements**
- Controller(s) and service(s) for enrollment start/confirm, wired through Spring Security to require authentication.
- DTOs for enrollment responses including provisioning URI/QR payload and recovery codes.
- Unit tests/service tests for enrollment confirmation behavior.

**Constraints**
- Keep controllers thin; put logic in services.
- No persistence of derived TOTP secrets or recovery codes—only derivation inputs and state flags.
- Respect tenant context handling already present in login/security layers.

**Human Intervention**
If QR generation requires new libraries, confirm they are acceptable within MS3 dependency policies.

**Definition of Done (DoD)**
- Enrollment start returns deterministic provisioning data and recovery codes.
- Enrollment confirm activates 2FA only after valid OTP; state is persisted via version/salt and flags.
- Tests cover confirmation and recovery code exposure rules.

## Codex Prompt — Login step 1: credential check + 2FA challenge (no TTL)
**Context**
Adjust `/login/` to run password verification first, then branch to a 2FA challenge response without issuing a JWT until OTP success.

**Files to Read**
- `src/main/java/org/cswteams/ms3/rest/LoginRestEndpoint.java`
- `src/main/java/org/cswteams/ms3/security/BlacklistService.java`
- `src/main/java/org/cswteams/ms3/utils/TurnstileService.java`
- `src/main/java/org/cswteams/ms3/filters/JwtRequestFilters.java`
- `docs/security/2fa_totp_design.md`

**Objectives**
- Modify `/login/` to return `requires2fa=true` plus a user-bound challenge marker when the user has 2FA enabled or is required by role; do not issue JWT yet.
- Ensure challenge markers are bound to the specific user/auth attempt (e.g., signed/hashed structure) without using a TTL; explain replay protection strategy.
- Maintain existing Turnstile/blacklist behavior for the password step.

**Output Requirements**
- Updated login controller/service responses and models to carry the 2FA challenge marker and enforcement flags.
- Documentation/comments clarifying that no challenge TTL exists and that security relies on OTP freshness + lockout + binding.
- Tests covering login responses for 2FA-enabled/required users and standard users.

**Constraints**
- Do not introduce a time-based challenge expiry.
- Preserve backward compatibility for users without 2FA (immediate JWT issuance on success).
- Keep blacklist/Turnstile logic intact for the password step.

**Human Intervention**
None expected.

**Definition of Done (DoD)**
- `/login/` returns challenge markers for 2FA-required cases without issuing JWTs.
- Challenge markers are user-bound and non-replayable for minting multiple JWTs.
- Existing login behavior remains unchanged for users without 2FA.

## Codex Prompt — OTP verification endpoint, lockout, and recovery handling
**Context**
Add OTP verification endpoint that consumes challenge markers, enforces attempt-count lockout, supports recovery codes with jump-ahead semantics, and issues JWT on success.

**Files to Read**
- `src/main/java/org/cswteams/ms3/rest/LoginRestEndpoint.java` (or new controller)
- `src/main/java/org/cswteams/ms3/security/BlacklistService.java`
- `src/main/java/org/cswteams/ms3/utils/JwtUtil.java`
- Services created for enrollment/derivation
- `docs/security/2fa_totp_design.md`

**Objectives**
- Implement `POST /login/otp` (or similar) that validates the challenge marker + OTP or recovery code, then issues JWT.
- Enforce server-side attempt-count lockout: after N failed OTP attempts, block further OTP verification for 60s; reset counters on success. Decide keying (user vs user+ip vs user+ip+tenant) aligned with existing blacklist patterns and document choice.
- Implement jump-ahead recovery code validation: accept code with id ≥ expected, update `lastRecoveryCodeIdUsed`, reject older codes; when id==N, disable 2FA and rotate `twoFaVersionOrSalt` for re-enrollment.
- Prevent OTP/recovery replay from minting multiple JWTs using the same challenge marker.

**Output Requirements**
- Controller/service logic for OTP verification, recovery code processing, and JWT issuance.
- Lockout storage mechanism (cache/DB) consistent with repository infrastructure, plus configuration for MAX_OTP_ATTEMPTS and OTP_LOCKOUT_SECONDS.
- Tests covering lockout entry/exit, jump-ahead acceptance, final-code disablement, and replay prevention.

**Constraints**
- No challenge TTLs; rely on OTP freshness and lockout policy.
- Error responses must avoid leaking whether TOTP or recovery was checked; include lockout remaining time if available.
- No logging of secrets or codes.

**Human Intervention**
None expected.

**Definition of Done (DoD)**
- OTP endpoint issues JWT only after successful OTP/recovery validation and lockout checks.
- Lockout enforced consistently across challenges; counters reset on success.
- Recovery codes honor jump-ahead semantics and deactivate 2FA on the final code.

## Codex Prompt — Role-based enforcement and audit/error handling
**Context**
Apply role-based 2FA enforcement while keeping responses safe and avoiding pre-password leakage. Add auditing where appropriate.

**Files to Read**
- `src/main/java/org/cswteams/ms3/entity/SystemUser.java` (roles)
- `src/main/java/org/cswteams/ms3/security/SecurityConfigurer.java`
- `src/main/java/org/cswteams/ms3/rest/LoginRestEndpoint.java`
- Any logging/auditing utilities already present in the auth flow
- `docs/security/2fa_totp_design.md`

**Objectives**
- Identify roles requiring 2FA (config-driven). Enforce enrollment/OTP requirement before issuing JWT for those roles.
- Ensure enforcement decisions are only revealed after successful password verification.
- Add audit logging for enrollment/disable events, failed OTP/recovery attempts, lockout triggers, and successful verifications.

**Output Requirements**
- Configuration for required roles and enforcement checks integrated into login/enrollment flows.
- Logging/auditing statements aligned with existing patterns (no sensitive data).
- Tests verifying enforced roles cannot bypass 2FA and optional users remain unaffected unless opted in.

**Constraints**
- Do not leak enforcement status pre-authentication.
- Keep logging free of secrets or recovery code material.

**Human Intervention**
Coordinate with security leads to confirm the list of enforced roles.

**Definition of Done (DoD)**
- Role-based enforcement blocks JWT issuance until 2FA is complete for required roles.
- Audit logs capture critical 2FA lifecycle events without sensitive data.
- Tests cover enforced/optional role scenarios.

## Codex Prompt — Frontend login OTP modal and lockout UX
**Context**
Update the React login flow to handle two-step authentication, lockout messaging, and safe JWT storage only after OTP success.

**Files to Read**
- `frontend/src/views/utente/LoginView.js`
- `frontend/src/API/LoginAPI.js`
- Any shared auth/context utilities in `frontend/src`
- `docs/security/2fa_totp_design.md`

**Objectives**
- Add client handling for `requires2fa=true` responses: store challenge marker in memory, show OTP/recovery input modal.
- Prevent JWT/localStorage writes until OTP verification succeeds; ensure role selection waits for full JWT.
- Display lockout responses with remaining wait time if provided; offer recovery code input path.

**Output Requirements**
- Updated login view/API hooks to manage the challenge marker and OTP verification calls.
- UI components/modal for OTP/recovery input with lockout messaging.
- Frontend tests (if present) for the two-step flow, lockout messaging, and JWT storage gating.

**Constraints**
- No storage of the challenge marker in localStorage/sessionStorage; keep it in component state/memory.
- Follow existing styling/component conventions; avoid new heavy dependencies.

**Human Intervention**
None expected.

**Definition of Done (DoD)**
- Login flow completes only after OTP success; JWT is persisted afterward.
- Lockout state/messages are shown when applicable.
- Tests (or manual verification plan) cover the two-step UX.

## Codex Prompt — Frontend enrollment & recovery UX
**Context**
Implement enrollment UI for QR/manual key display, OTP confirmation, recovery code presentation, and explain jump-ahead + final-code disablement semantics.

**Files to Read**
- `frontend/src` components/pages for user settings/profile
- API helpers for authenticated requests
- `docs/security/2fa_totp_design.md`

**Objectives**
- Add UI to initiate enrollment, show QR/manual key, accept OTP for confirmation, and display recovery codes once.
- Provide clear copy about jump-ahead recovery semantics and that the final code disables 2FA and requires re-enrollment.
- Support role-enforced prompts guiding users to enroll when required.

**Output Requirements**
- New/updated components and API calls for enrollment start/confirm and disable flows.
- UX copy explaining recovery code behavior and final-code disablement.
- Frontend tests (if present) covering enrollment success/failure and recovery code display rules.

**Constraints**
- Do not cache recovery codes beyond initial display; avoid logging them.
- Maintain consistency with existing styling and routing patterns.

**Human Intervention**
Coordinate with UX stakeholders for copy approval if necessary.

**Definition of Done (DoD)**
- Enrollment UI completes the flow and surfaces recovery codes once.
- Copy clearly explains jump-ahead behavior and final-code disablement.
- Tests or manual QA steps validate enrollment UX.

## Codex Prompt — Backend tests for lockout, recovery jump-ahead, and final-code disablement
**Context**
Add targeted backend tests to ensure the new 2FA behaviors are enforced, focusing on lockout, recovery semantics, and JWT issuance gating.

**Files to Read**
- Backend test suites under `src/test/java/` relevant to auth/login
- Services/controllers created in earlier prompts
- `docs/security/2fa_totp_design.md`

**Objectives**
- Create integration/unit tests covering: OTP lockout after N failures and release after 60s; acceptance of recovery code with id ≥ expected; rejection of stale codes; final code disabling 2FA and rotating version/salt; prevention of JWT issuance without OTP for required roles.
- Mock or stub time as needed to simulate the 60s lockout window without flakiness.

**Output Requirements**
- New/updated test classes with clear naming and assertions for each scenario.
- Test data/builders for users with/without enforced roles and 2FA states.

**Constraints**
- Keep tests CI-stable; avoid flaky timing by using controllable clocks or injected time sources.
- Maintain existing test frameworks/dependencies.

**Human Intervention**
None expected.

**Definition of Done (DoD)**
- Tests cover lockout, jump-ahead recovery, final-code disablement, and role enforcement gating.
- Test suite passes locally for affected modules.

## Codex Prompt — Docs/config updates and operational runbook
**Context**
Document environment/config changes, operational procedures, and UX/security notes for the new 2FA implementation.

**Files to Read**
- `README.md`
- `docs/security/2fa_totp_design.md`
- Any ops/dev docs under `docs/`

**Objectives**
- Update documentation to include new env vars (HMAC master key, MAX_OTP_ATTEMPTS, OTP_LOCKOUT_SECONDS, enforced roles, recovery code count), operational guidance, and UX expectations.
- Provide runbook notes for lockout handling, recovery code jump-ahead behavior, and final-code disablement/re-enrollment steps.
- Ensure docs note the absence of challenge TTL and reliance on OTP freshness + lockout.

**Output Requirements**
- Updated docs reflecting configuration, deployment steps, and troubleshooting.
- No code changes in this prompt.

**Constraints**
- Keep documentation concise and aligned with actual implementation.

**Human Intervention**
Verify secure handling of secrets in deployment pipelines.

**Definition of Done (DoD)**
- Documentation clearly explains configuration, lockout policy, recovery semantics, and no-TTL challenge design.
- Ops teams can deploy and support the feature based on the runbook.

## DB Environment Setup Prompts (TOTP 2FA)
- **Where this fits**: Run these prompts **after** completing the earlier “DB schema for 2FA state & lockout” prompt to ensure the new SQL is actually executed across environments.

## Codex Prompt — Wire 2FA schema changes into the existing schema initializer
**Context**
`SchemasInitializer` currently runs raw SQL scripts at startup to drop/recreate schemas and seed tables for the public and tenant schemas (A/B), instead of Flyway/Liquibase. It executes `clean_default_schema.sql`, `create_schemas.sql`, then invokes per-schema table scripts under `src/main/resources/db/` using `ScriptUtils` and `SET search_path` to move across schemas. The new `db/migration/V1__add_2fa_state_columns.sql` is not wired into this flow, and Hibernate DDL auto is disabled in `HibernateMultiTenancyConfig` (DDL is set to `none`).

**Files to Read**
- `src/main/java/org/cswteams/ms3/config/multitenancy/SchemasInitializer.java`
- `src/main/java/org/cswteams/ms3/config/multitenancy/HibernateMultiTenancyConfig.java`
- `src/main/resources/db/create_system_user_tables.sql`
- `src/main/resources/db/tenant/tables/create_tenant_user_tables.sql`
- `src/main/resources/db/migration/V1__add_2fa_state_columns.sql`
- `src/main/resources/tenants_config.json`

**Objectives**
- Ensure 2FA schema updates run automatically for **all schemas** that `SchemasInitializer` touches (public + each tenant from `tenants_config.json`), not just the public schema.
- Decide whether to fold the new columns directly into the per-schema create scripts, invoke the `db/migration/V1__add_2fa_state_columns.sql` during initialization, or both; document the choice and keep execution idempotent when schemas already exist.
- Keep `SET search_path`/schema switching consistent with the current `createTablesForTenant`/`createTablesInPublicSchema` flow so the new SQL executes in the right schema.

**Output Requirements**
- Update `SchemasInitializer` and/or the relevant SQL scripts so a fresh startup applies the 2FA columns everywhere without manual intervention; retain the current multi-tenant loop and error handling conventions.
- If using the migration file, ensure it is invoked for each schema; if inlining into table create scripts, keep migration file in sync or document its role (e.g., for existing DBs).
- Add brief inline comments explaining how the new 2FA SQL is executed during bootstrap and how to extend it when new tenants are added.

**Constraints**
- Do not introduce Flyway/Liquibase; stay within the existing `ScriptUtils` initializer pattern and search_path switching.
- Keep scripts compatible with current role-based ownership/permissions applied later via `assign_privileges.sql`.

**Human Intervention**
- None expected beyond confirming tenant lists.

**Definition of Done (DoD)**
- Fresh application startup creates/appends the 2FA columns in public and tenant schemas with no manual SQL.
- Existing environments have a documented migration path (via the migration script or initializer) without breaking existing data.
- Multi-tenant schema switching and privileges continue to work unchanged.

## Codex Prompt — Align docker/local DB bootstrap with 2FA schema changes
**Context**
Local/docker environments rely on the Postgres image built from `src/main/resources/db/Dockerfile.db`, `docker-compose.yml`, and entrypoint scripts mounted from `src/main/resources/db/init-scripts` (e.g., `init-users.sh`) to provision roles before the app’s `SchemasInitializer` runs. 2FA DB changes must be compatible with this pipeline so the app can create/update schemas using the correct users/passwords.

**Files to Read**
- `docker-compose.yml`
- `src/main/resources/db/Dockerfile.db`
- `src/main/resources/db/init-scripts/init-users.sh`
- `src/main/resources/application-container.properties`
- `src/main/resources/tenants_config.json`

**Objectives**
- Ensure docker-compose/local bring-up provisions the DB users/roles (public/tenant A/tenant B) and permissions needed for the initializer to apply the 2FA SQL across schemas.
- Verify environment variables (`DB_TENANT_*`, `DB_USER`, `DB_PASSWORD`) are documented and mapped consistently between compose, init scripts, and Spring properties so the initializer can connect and run the scripts.
- Add guidance on rebuilding/reseeding volumes when schema changes are introduced (e.g., dropping volumes or running migration scripts manually) to avoid stale containers missing the 2FA columns.

**Output Requirements**
- Update docker/db init documentation/comments (compose file and/or init script headers) to describe the required env vars and how 2FA schema changes are applied during local startup.
- If necessary, adjust init scripts to ensure role creation/permissions align with any new schemas involved in 2FA (still avoiding SQL*Plus-style directives; use plain SQL via psql heredoc).

**Constraints**
- No application code changes; keep SQL portable for the Postgres image.
- Do not alter the multi-tenant list beyond what is defined in `tenants_config.json` without explicit instruction.

**Human Intervention**
- Developers may need to prune `db_data` volumes when testing schema changes; document this if required.

**Definition of Done (DoD)**
- `docker-compose up` from a clean environment produces a database where the 2FA columns exist in public and tenant schemas.
- Role/password wiring still matches Spring datasource settings so the initializer can run without permission errors.

## Codex Prompt — Ensure test/CI profiles execute the 2FA DB setup
**Context**
Tests/CI rely on the same Spring Boot app to spin up schemas. `HibernateMultiTenancyConfig` forces `hibernate.ddl-auto` to `none`, so the `SchemasInitializer` is the only path to create tables. The container profile uses `${db.*}` properties for datasource credentials.

**Files to Read**
- `src/main/java/org/cswteams/ms3/config/multitenancy/SchemasInitializer.java`
- `src/main/resources/application.properties`
- `src/main/resources/application-container.properties`
- Any test bootstrap configuration under `src/test` (if added for this task)

**Objectives**
- Confirm integration tests (or future ones for 2FA) automatically get the 2FA columns by running the initializer; document how to point tests at a disposable Postgres and ensure the initializer runs once per test context.
- Add profile notes so test/CI runs don’t accidentally enable `hibernate.ddl-auto=create` or bypass the initializer.
- Provide guidance for handling parallel tests/tenants if they are added (e.g., unique schemas vs. shared A/B), ensuring 2FA SQL is applied in each schema used by tests.

**Output Requirements**
- Update documentation/comments in properties files or a short test README note (if appropriate) explaining how tests should bootstrap the DB with the initializer and where to set datasource credentials.
- If needed, add a minimal test profile properties snippet to enforce initializer execution and correct tenant DS config; no code changes to test cases are required here.

**Constraints**
- Do not add Flyway/Liquibase to tests; stay consistent with runtime bootstrap.
- Keep instructions lean; no new test suites are added in this prompt.

**Human Intervention**
- Test engineers may need to start/stop a Postgres container manually for CI; document expected credentials/schema state.

**Definition of Done (DoD)**
- Guidance exists so future 2FA tests start against a database that already includes the 2FA columns in all active schemas.
- Test/CI profiles continue to use the multi-tenant datasource settings without regressing to Hibernate DDL auto-creation.
