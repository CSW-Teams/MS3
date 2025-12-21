create table ms3_tenant_user
(
    ms3_tenant_user_id bigint       not null
        primary key,
    birthday           date         not null,
    email              varchar(255) not null,
    lastname           varchar(255) not null,
    name               varchar(255) not null,
    password           varchar(255) not null,
    two_fa_version_or_salt varchar(255),
    two_factor_enabled boolean      default false not null,
    enrollment_confirmed_at timestamp,
    last_recovery_code_id_used integer default 0 not null,
    otp_failed_attempts integer        default 0 not null,
    otp_lockout_ends_at timestamp,
    otp_last_attempt_at timestamp,
    tax_code           varchar(255) not null
);


create table user_tenant_actors
(
    user_ms3_tenant_user_id bigint not null,
    system_actors           integer
);


create table tenantuser_systemactors
(
    ms3_tenant_user_id bigint      not null,
    role               varchar(50) not null,
    primary key (ms3_tenant_user_id, role)
);