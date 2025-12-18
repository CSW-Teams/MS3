# Test/CI database bootstrap with SchemasInitializer

Integration tests rely on `SchemasInitializer` to create schemas, tables, and the 2FA columns (`db/migration/V1__add_2fa_state_columns.sql`) across every tenant. Hibernate DDL generation is disabled at runtime, so tests must point at a Postgres instance that the initializer can reach when the Spring context starts.

## Starting a disposable Postgres for tests

Start a throwaway Postgres locally or in CI using the same environment variables consumed by the `container` profile:

```bash
# Example: single database with tenants public/a/b. Remove the volume to reset schema state
# (e.g., docker rm -f ms3-test-db && docker volume rm ms3-test-db-data).
docker run --name ms3-test-db \
  -e POSTGRES_DB=${DB_NAME:-ms3} \
  -e POSTGRES_USER=${DB_USER:-sprintfloyd} \
  -e POSTGRES_PASSWORD=${DB_PASSWORD:-sprintfloyd} \
  -e DB_TENANT_PUBLIC_USER=${DB_TENANT_PUBLIC_USER:-public_scheme_user} \
  -e DB_TENANT_PUBLIC_PASSWORD=${DB_TENANT_PUBLIC_PASSWORD:-password_public} \
  -e DB_TENANT_A_USER=${DB_TENANT_A_USER:-tenant_a_user} \
  -e DB_TENANT_A_PASSWORD=${DB_TENANT_A_PASSWORD:-password_a} \
  -e DB_TENANT_B_USER=${DB_TENANT_B_USER:-tenant_b_user} \
  -e DB_TENANT_B_PASSWORD=${DB_TENANT_B_PASSWORD:-password_b} \
  -p 5432:5432 -d postgres:14
```

The `init-scripts` in `src/main/resources/db` create the tenant roles. When the Spring context boots, `SchemasInitializer` runs once and applies the 2FA SQL to the `public`, `a`, and `b` schemas (or any schemas declared in `tenants_config.json`).

## Running tests against the initializer

1. Export the database variables used by `application-container.properties` (e.g., `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_TENANT_*`).
2. Launch tests with both `container` and `test` profiles so the multi-tenant datasource placeholders resolve and Hibernate DDL stays disabled:

   ```bash
   SPRING_PROFILES_ACTIVE=container,test mvn -DskipFrontendTests=true test
   ```

   `spring.jpa.hibernate.ddl-auto` remains `none`, ensuring Hibernate does not bypass `SchemasInitializer`.

## Parallel or tenant-isolated test runs

- **Separate databases**: give each worker a unique `${DB_NAME}` (or Postgres container) so `SchemasInitializer` applies the 2FA columns in isolation per run.
- **Shared database with isolated schemas**: extend `tenants_config.json` with schema names that encode the worker ID (for example, `tenant_w1`, `tenant_w2`). The initializer is idempotent and will apply `V1__add_2fa_state_columns.sql` to every schema listed.
- Always reset the Postgres data directory (drop the container or volume) if table definitions change; this guarantees the initializer replays from a clean slate.
