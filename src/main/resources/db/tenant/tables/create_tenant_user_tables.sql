create table ms3_tenant_user
(
    ms3_tenant_user_id bigint       not null
        primary key,
    birthday           date         not null,
    email              varchar(255) not null,
    lastname           varchar(255) not null,
    name               varchar(255) not null,
    password           varchar(255) not null,
    tax_code           varchar(255) not null
);

alter table ms3_tenant_user
    owner to sprintfloyd;


create table user_tenant_actors
(
    user_ms3_tenant_user_id bigint not null,
    system_actors           integer
);

alter table user_tenant_actors
    owner to sprintfloyd;


create table tenantuser_systemactors
(
    ms3_tenant_user_id bigint      not null,
    role               varchar(50) not null,
    primary key (ms3_tenant_user_id, role)
);

alter table tenantuser_systemactors
    owner to sprintfloyd;