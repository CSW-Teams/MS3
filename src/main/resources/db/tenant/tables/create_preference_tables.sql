create table preference
(
    id   bigint not null
        primary key,
    date date
);

alter table preference
    owner to sprintfloyd;


create table preference_doctors
(
    preference_id              bigint not null
        constraint fkhrgatrsccsp0kry9kb9ft8sh0
            references preference,
    doctors_ms3_system_user_id bigint not null
        constraint fkyqbccg77ndmev6jov5awru6t
            references doctor
);

alter table preference_doctors
    owner to sprintfloyd;


create table preference_time_slots
(
    preference_id bigint not null
        constraint fk41q4kloomdt3wyixirwn3sudt
            references preference,
    time_slots    integer
);

alter table preference_time_slots
    owner to sprintfloyd;

