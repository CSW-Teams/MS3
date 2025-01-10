create table config
(
    name       varchar(255) not null
        primary key,
    first_boot boolean      not null
);

alter table config
    owner to sprintfloyd;


create table config_vinc_max_per_cons
(
    max_periodo_id           bigserial
        primary key,
    max_consecutive_minutes  integer not null,
    constrained_condition_id bigint  not null
        constraint fk3tnkfvlqsgpkjlyn5h40jtad9
            references condition
);

alter table config_vinc_max_per_cons
    owner to sprintfloyd;


create table config_vincoli
(
    config_vincoli_id                 bigserial
        primary key,
    horizon_night_shift               integer not null,
    max_consecutive_time_for_everyone integer not null,
    period_days_no                    integer not null,
    period_max_time                   integer not null
);

alter table config_vincoli
    owner to sprintfloyd;


create table config_vincoli_config_vinc_max_per_cons_per_categoria
(
    config_vincoli_config_vincoli_id                      bigint not null
        constraint fkcd06cj6l9aw4qvhkpr4f9kvm2
            references config_vincoli,
    config_vinc_max_per_cons_per_categoria_max_periodo_id bigint not null
        constraint uk_7rtyc4098bip4hsr621xsbmuq
            unique
        constraint fkj89no7ryhto0pi3831yp1hdgs
            references config_vinc_max_per_cons
);

alter table config_vincoli_config_vinc_max_per_cons_per_categoria
    owner to sprintfloyd;

