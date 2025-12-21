#!/bin/bash
set -e

# Bootstrap Postgres roles so the Spring SchemasInitializer can create/alter schemas
# (including 2FA columns) for each tenant during container startup. All credentials
# are provided by docker-compose via environment variables and must stay aligned with
# application-container.properties placeholders.
#
# Required environment variables:
# - POSTGRES_USER / POSTGRES_DB: superuser and database created by the base image.
# - DB_TENANT_PUBLIC_USER / DB_TENANT_PUBLIC_PASSWORD: login used for the public schema.
# - DB_TENANT_A_USER / DB_TENANT_A_PASSWORD: login used for tenant A.
# - DB_TENANT_B_USER / DB_TENANT_B_PASSWORD: login used for tenant B.
#
# When schema changes are introduced (e.g., new 2FA columns), prune the db_data volume
# or recreate the container so this script and the initializer run on a clean database.

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
