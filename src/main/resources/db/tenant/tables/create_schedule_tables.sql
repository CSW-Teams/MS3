create table schedule
(
    schedule_id   bigint not null
        primary key,
    cause_illegal bytea,
    end_date      bigint not null,
    start_date    bigint not null
);


create table schedule_concrete_shifts
(
    schedule_schedule_id              bigint not null
        constraint fk1nccylsrcvlpbcfpij716ejrk
            references schedule,
    concrete_shifts_concrete_shift_id bigint not null
        constraint uk_cw9jkumdwnmyt684g8dxtbi13
            unique
        constraint fkaily47udtim6bm3u1pt16qi6j
            references concrete_shift
);


create table schedule_violated_constraints
(
    schedule_schedule_id               bigint not null
        constraint fko3aerkdgl1ucibcal8iwiiq0u
            references schedule,
    violated_constraints_constraint_id bigint not null
        constraint fkcrmao7o7l611d63pw7jcgp8dl
            references ms3_constraint
);

create table user_schedule_state
(
    id                        bigint  not null
        primary key,
    uffa_cumulativo           integer not null,
    uffa_parziale             integer not null,
    schedule_schedule_id      bigint
        constraint fkrw089fxqcwklfx8g9tetvt4jd
            references schedule,
    utente_ms3_tenant_user_id bigint
);

