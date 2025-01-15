create table notification
(
    notification_id         bigint       not null
        primary key,
    message                 varchar(255) not null,
    status                  boolean      not null,
    user_ms3_tenant_user_id bigint       not null
);

