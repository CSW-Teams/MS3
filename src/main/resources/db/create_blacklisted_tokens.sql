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
    foreign key (ms3_system_user_id) references ms3_system_user(ms3_system_user_id)
);