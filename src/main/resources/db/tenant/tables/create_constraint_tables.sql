create table ms3_constraint
(
    dtype                   varchar(31)  not null,
    constraint_id           bigint       not null
        primary key,
    description             varchar(255) not null,
    violable                boolean      not null,
    horizon                 integer,
    t_unit                  varchar(255),
    time_slot               varchar(255),
    max_consecutive_minutes bigint,
    period_duration         integer,
    period_max_time         bigint,
    constrained_category_id bigint
        constraint fk56q3fx7puxy8fecicqeugo8cv
            references condition
);


create table constraint_turni_contigui_forbidden_time_slots
(
    constraint_turni_contigui_constraint_id bigint not null
        constraint fkjrdi3549kx7mwtr47m0fkk098
            references ms3_constraint,
    forbidden_time_slots                    varchar(255)
);

create table violated_constraint_log_entry
(
    id        bigint not null
        primary key,
    violation bytea
);

