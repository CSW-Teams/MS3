-- Purpose: persist revoked JWTs shared by all tenants for stateless logout and token invalidation.
-- Context: executed in schema public by SchemasInitializer after ms3_system_user exists.
-- Order: run after create_system_user_tables.sql and before assign_privileges.sql grants.
create table blacklisted_tokens
(
    id bigserial
        primary key,
    token varchar(512) not null
        constraint uk_blacklisted_tokens_token
            unique,
    blacklisted_at timestamp not null,
    expires_at timestamp not null,
    ms3_system_user_id bigint not null,
    -- Assumption: user identity stays in public schema; tenant schemas reference this shared id.
    foreign key (ms3_system_user_id) references ms3_system_user(ms3_system_user_id)
);
