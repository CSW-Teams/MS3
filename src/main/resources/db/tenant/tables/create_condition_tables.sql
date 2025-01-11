create table condition
(
    id   bigserial
        primary key,
    type varchar(255) not null
);

alter table condition
    owner to sprintfloyd;


create table permanent_condition
(
    id bigint not null
        primary key
        constraint fkidbh8f1v99ueewy58lfajs3g3
            references condition
);

alter table permanent_condition
    owner to sprintfloyd;


create table temporary_condition
(
    end_date   bigint not null,
    start_date bigint not null,
    id         bigint not null
        primary key
        constraint fkb303kbmim9pt133tyojbnay6m
            references condition
);

alter table temporary_condition
    owner to sprintfloyd;

