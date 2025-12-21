# Logout – Sprint 1 Summary

## Goal
Introduce a robust logout mechanism that invalidates JWTs server-side, ensure the shared blacklist table is reachable by all tenants, and harden login with a fallback security check to mitigate brute-force attempts.

## Backend changes
- Implemented persistent JWT blacklisting: a new `BlacklistedToken` entity/DAO pair backed by the `blacklisted_tokens` table stores invalidated tokens with timestamps. 【F:src/main/java/org/cswteams/ms3/entity/BlacklistedToken.java†L12-L31】【F:src/main/java/org/cswteams/ms3/dao/BlacklistedTokenDAO.java†L1-L11】
- Added `JwtBlacklistService` to encapsulate blacklist persistence and validation, wiring it into `LogoutController` and the logout REST endpoint so bearer tokens are recorded on logout. 【F:src/main/java/org/cswteams/ms3/control/logout/JwtBlacklistService.java†L16-L53】【F:src/main/java/org/cswteams/ms3/rest/LogoutRestEndpoint.java†L18-L37】
- Updated the JWT request filter to block blacklisted tokens early and emit a 401 response, and exposed an authenticated logout route in the security config. 【F:src/main/java/org/cswteams/ms3/filters/JwtRequestFilters.java†L52-L106】【F:src/main/java/org/cswteams/ms3/security/SecurityConfigurer.java†L43-L75】
- Provided a shared `Clock` bean to make time-stamping deterministic and testable for blacklist operations. 【F:src/main/java/org/cswteams/ms3/config/AppConfig.java†L56-L69】

## Frontend changes
- Added a reusable Cloudflare Turnstile widget that injects/cleans the script, exposes reset via `ref`, and shows loading copy through i18n. 【F:frontend/src/components/common/TurnstileWidget.js†L10-L102】
- Enhanced the login view to display the Turnstile challenge after 400/401 responses, pass the token with credentials, and reset the widget when the captcha must be retried. 【F:frontend/src/views/utente/LoginView.js†L117-L197】【F:frontend/src/views/utente/LoginView.js†L244-L269】
- Added localization strings for the new security-check UX. 【F:frontend/src/locales/en.json†L30-L48】

## Authentication hardening
- Backend login endpoint now checks an in-memory blacklist for repeated failures by IP/email, requires a Turnstile token when throttling is triggered, and validates it via `TurnstileService` before authenticating. 【F:src/main/java/org/cswteams/ms3/rest/LoginRestEndpoint.java†L50-L117】【F:src/main/java/org/cswteams/ms3/utils/TurnstileService.java†L14-L50】

## Database and multitenancy adjustments
- Schema initializer now creates the shared `blacklisted_tokens` table, and privilege scripts grant tenant users insert/select plus sequence usage so logout blacklisting works across schemas. 【F:src/main/resources/db/create_blacklisted_tokens.sql†L1-L9】【F:src/main/resources/db/assign_privileges.sql†L28-L52】
- Multi-tenant connection provider sets `search_path` to include both the tenant schema and `public`, ensuring shared tables remain visible to tenant-specific connections. 【F:src/main/java/org/cswteams/ms3/config/multitenancy/SchemaSwitchingConnectionProviderPostgreSQL.java†L34-L50】

## Test coverage
- Unit and integration tests verify logout blacklists tokens and deny subsequent access with the invalidated JWT; includes full flow login→logout→protected-resource checks. 【F:src/test/java/org/cswteams/ms3/control/logout/TestLogoutIT.java†L14-L55】【F:src/test/java/org/cswteams/ms3/rest/LogoutRestEndpointIT.java†L29-L62】

## Deployment/configuration notes
- Backend expects Cloudflare Turnstile secret and verification URL (`cloudflare.turnstile.secret`/`url`) in application properties, and the frontend uses `REACT_APP_TURNSTILE_KEY` for rendering the widget. 【F:src/main/resources/application.properties†L35-L37】【F:frontend/src/views/utente/LoginView.js†L244-L251】
- Logout blacklisting relies on the shared `blacklisted_tokens` table residing in the `public` schema; tenant DB users must retain the granted privileges to insert and read from it. 【F:src/main/resources/db/assign_privileges.sql†L28-L52】

## Risks and follow-ups
- Ensure cache/config alignment: keep Turnstile keys synchronized between environments and rotate secrets as needed.
- Monitor blacklist table growth; consider retention/cleanup if tokens accumulate beyond operational expectations.
- Verify UI turnstile rendering in production builds where script caching and CSP headers could differ from development.
