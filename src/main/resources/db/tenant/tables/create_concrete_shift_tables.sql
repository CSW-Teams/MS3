create table concrete_shift
(
    concrete_shift_id bigint not null
        primary key,
    date              bigint not null,
    shift_shift_id    bigint not null
        constraint fkjbedgfkknt1v42vexkh8h56d8
            references shift
);

alter table concrete_shift
    owner to sprintfloyd;
