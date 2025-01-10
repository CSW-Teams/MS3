create table scocciatura
(
    dtype            varchar(31) not null,
    id               bigint      not null
        primary key,
    peso             integer,
    time_slot        integer,
    giorno_settimana integer,
    vacanza_id       bigint
        constraint fklmak2608yw4bmoy9rb9vp3f28
            references holiday
            on delete cascade
);

alter table scocciatura
    owner to sprintfloyd;

