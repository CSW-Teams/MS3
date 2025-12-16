revoke all on schema public from public;
revoke all on all tables in schema public from public;
revoke all on all sequences in schema public from public;

revoke all on schema a from public;
revoke all on all tables in schema a from public;
revoke all on all sequences in schema a from public;

revoke all on schema b from public;
revoke all on all tables in schema b from public;
revoke all on all sequences in schema b from public;

grant all on schema public to public_scheme_user;
grant all on all tables in schema public to public_scheme_user;
grant all on all sequences in schema public to public_scheme_user;
alter schema public owner to public_scheme_user;

grant all on schema a to tenant_a_user;
grant all on all tables in schema a to tenant_a_user;
grant all on all sequences in schema a to tenant_a_user;
alter schema a owner to tenant_a_user;

grant all on schema b to tenant_b_user;
grant all on all tables in schema b to tenant_b_user;
grant all on all sequences in schema b to tenant_b_user;
alter schema b owner to tenant_b_user;

-- Grants to the tenant users for the 'blacklisted_tokens' table in the public schema
grant usage on schema public to tenant_a_user;
grant usage on schema public to tenant_b_user;
grant select, insert on public.blacklisted_tokens to tenant_a_user;
grant select, insert on public.blacklisted_tokens to tenant_b_user;

alter default privileges in schema public revoke all on tables from public;
alter default privileges in schema public grant all on tables to public_scheme_user;
alter default privileges in schema public revoke all on sequences from public;
alter default privileges in schema public grant all on sequences to public_scheme_user;

alter default privileges in schema a revoke all on tables from public;
alter default privileges in schema a grant all on tables to tenant_a_user;
alter default privileges in schema a revoke all on sequences from public;
alter default privileges in schema a grant all on sequences to tenant_a_user;

alter default privileges in schema b revoke all on tables from public;
alter default privileges in schema b grant all on tables to tenant_b_user;
alter default privileges in schema b revoke all on sequences from public;
alter default privileges in schema b grant all on sequences to tenant_b_user;

DO $$
    DECLARE
        tbl RECORD;
    BEGIN
        FOR tbl IN
            SELECT tablename
            FROM pg_tables
            WHERE schemaname = 'public'
            LOOP
                EXECUTE format('ALTER TABLE %I.%I OWNER TO public_scheme_user', 'public', tbl.tablename);
            END LOOP;
    END $$;;

DO $$
    DECLARE
        tbl RECORD;
    BEGIN
        FOR tbl IN
            SELECT tablename
            FROM pg_tables
            WHERE schemaname = 'a'
            LOOP
                EXECUTE format('ALTER TABLE %I.%I OWNER TO tenant_a_user', 'a', tbl.tablename);
            END LOOP;
    END $$;;

DO $$
    DECLARE
        tbl RECORD;
    BEGIN
        FOR tbl IN
            SELECT tablename
            FROM pg_tables
            WHERE schemaname = 'b'
            LOOP
                EXECUTE format('ALTER TABLE %I.%I OWNER TO tenant_b_user', 'b', tbl.tablename);
            END LOOP;
    END $$;;

DO $$
    DECLARE
        seq RECORD;
    BEGIN
        FOR seq IN
            SELECT sequencename
            FROM pg_sequences
            WHERE schemaname = 'public'
            LOOP
                EXECUTE format('ALTER SEQUENCE %I.%I OWNER TO public_scheme_user', 'public', seq.sequencename);
            END LOOP;
    END $$;;

DO $$
    DECLARE
        seq RECORD;
    BEGIN
        FOR seq IN
            SELECT sequencename
            FROM pg_sequences
            WHERE schemaname = 'a'
            LOOP
                EXECUTE format('ALTER SEQUENCE %I.%I OWNER TO tenant_a_user', 'a', seq.sequencename);
            END LOOP;
    END $$;;

DO $$
    DECLARE
        seq RECORD;
    BEGIN
        FOR seq IN
            SELECT sequencename
            FROM pg_sequences
            WHERE schemaname = 'b'
            LOOP
                EXECUTE format('ALTER SEQUENCE %I.%I OWNER TO tenant_b_user', 'b', seq.sequencename);
            END LOOP;
    END $$;;