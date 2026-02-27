#!/bin/sh
set -e

# Purpose: create runtime DB roles used by public and tenant schemas.
# Context: executed by the Postgres container init phase on a fresh database.
# Order: run before Spring SchemasInitializer scripts (schema/table creation and grants).
# Dependency: variable names must match application-container.properties placeholders.

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE ROLE ${DB_TENANT_PUBLIC_USER} WITH
        LOGIN
        NOSUPERUSER
        NOCREATEDB
        NOCREATEROLE
        INHERIT
        NOREPLICATION
        NOBYPASSRLS
        CONNECTION LIMIT -1
        PASSWORD '${DB_TENANT_PUBLIC_PASSWORD}';

    GRANT CONNECT ON DATABASE ${POSTGRES_DB} TO ${DB_TENANT_PUBLIC_USER};
    -- CREATE is required because SchemasInitializer creates/updates schema objects at startup.
    GRANT CREATE ON DATABASE ${POSTGRES_DB} TO ${DB_TENANT_PUBLIC_USER};

    CREATE ROLE ${DB_TENANT_A_USER} WITH
        LOGIN
        NOSUPERUSER
        NOCREATEDB
        NOCREATEROLE
        INHERIT
        NOREPLICATION
        NOBYPASSRLS
        CONNECTION LIMIT -1
        PASSWORD '${DB_TENANT_A_PASSWORD}';

    GRANT CONNECT ON DATABASE ${POSTGRES_DB} TO ${DB_TENANT_A_USER};
    GRANT CREATE ON DATABASE ${POSTGRES_DB} TO ${DB_TENANT_A_USER};

    CREATE ROLE ${DB_TENANT_B_USER} WITH
        LOGIN
        NOSUPERUSER
        NOCREATEDB
        NOCREATEROLE
        INHERIT
        NOREPLICATION
        NOBYPASSRLS
        CONNECTION LIMIT -1
        PASSWORD '${DB_TENANT_B_PASSWORD}';

    GRANT CONNECT ON DATABASE ${POSTGRES_DB} TO ${DB_TENANT_B_USER};
    GRANT CREATE ON DATABASE ${POSTGRES_DB} TO ${DB_TENANT_B_USER};
EOSQL
