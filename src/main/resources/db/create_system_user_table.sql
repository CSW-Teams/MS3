create table ms3_system_user
(
    ms3_system_user_id bigint       not null
        primary key,
    birthday           date         not null,
    email              varchar(255) not null,
    lastname           varchar(255) not null,
    name               varchar(255) not null,
    password           varchar(255) not null,
    tax_code           varchar(255) not null,
    tenant             varchar(255) not null
);

alter table ms3_system_user
    owner to sprintfloyd;