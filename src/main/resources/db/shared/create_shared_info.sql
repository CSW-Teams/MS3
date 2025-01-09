create sequence hibernate_sequence;
alter sequence hibernate_sequence owner to sprintfloyd;


create table systemuser_systemactors
(
    ms3_system_user_id bigint      not null,
    role               varchar(50) not null,
    primary key (ms3_system_user_id, role)
);

alter table systemuser_systemactors
    owner to sprintfloyd;


create table hibernate_sequences
(
    sequence_name varchar(255) not null
        primary key,
    next_val      bigint
);

alter table hibernate_sequences
    owner to sprintfloyd;