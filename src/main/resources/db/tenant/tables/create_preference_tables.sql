create table preference
(
    id   bigint not null
        primary key,
    date date
);


create table preference_doctors
(
    preference_id              bigint not null
        constraint fkhrgatrsccsp0kry9kb9ft8sh0
            references preference,
    doctors_ms3_tenant_user_id bigint not null
        constraint fkyqbccg77ndmev6jov5awru6t
            references doctor
);


create table preference_time_slots
(
    preference_id bigint not null
        constraint fk41q4kloomdt3wyixirwn3sudt
            references preference,
    time_slots    integer
);

