-- Purpose: create shared identity tables used by authentication and authorization modules.
-- Context: executed in schema public during bootstrap before tenant table creation.
-- Order: run before create_blacklisted_tokens.sql and before tenant grants in assign_privileges.sql.
create table ms3_system_user
(
    ms3_system_user_id bigint       not null
        primary key,
    birthday           date         not null,
    email              varchar(255) not null,
    lastname           varchar(255) not null,
    name               varchar(255) not null,
    password           varchar(255) not null,
    -- 2FA seed/version supports OTP secret rotation without forced account recreation.
    two_fa_version_or_salt varchar(255),
    -- False by default so legacy users can enroll gradually.
    two_factor_enabled boolean      default false not null,
    -- Enrollment timestamp proves OTP setup completion before enforcing OTP challenges.
    enrollment_confirmed_at timestamp,
    -- Stores latest accepted recovery code index to block code reuse.
    last_recovery_code_id_used integer default 0 not null,
    -- Tracks failed OTP attempts to enforce brute-force lockout.
    otp_failed_attempts integer        default 0 not null,
    -- End of temporary lockout window after too many OTP failures.
    otp_lockout_ends_at timestamp,
    -- Last OTP attempt timestamp supports monitoring and lockout calculations.
    otp_last_attempt_at timestamp,
    tax_code           varchar(255) not null,
    tenant             varchar(255) not null
);

create table user_system_actors
(
    user_ms3_system_user_id bigint not null,
    system_actors           integer
);

create table systemuser_systemactors
(
    ms3_system_user_id bigint      not null,
    role               varchar(50) not null,
    primary key (ms3_system_user_id, role)
);
