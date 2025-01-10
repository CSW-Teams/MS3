create table quantity_shift_seniority
(
    id            bigint not null
        primary key,
    seniority_map oid,
    task_task_id  bigint
        constraint fkqkkefb28jj74gk9as5lujuoht
            references task
);

alter table quantity_shift_seniority
    owner to sprintfloyd;

