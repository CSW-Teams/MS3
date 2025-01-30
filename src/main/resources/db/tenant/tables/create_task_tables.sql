create table task
(
    task_id   bigint not null
        primary key,
    task_type integer
);


create table task_with_assignmentdto
(
    task_id     bigint  not null
        primary key,
    is_assigned boolean not null,
    task_type   integer
);

