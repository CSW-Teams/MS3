create table blacklisted_tokens
(
    id bigserial
        primary key,
    token varchar(512) not null
        constraint uk_blacklisted_tokens_token
            unique,
    blacklisted_at timestamp not null
);