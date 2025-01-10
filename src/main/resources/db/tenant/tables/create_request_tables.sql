create table request
(
    id                          bigint not null
        primary key,
    status                      integer,
    receiver_ms3_system_user_id bigint not null,
    sender_ms3_system_user_id   bigint not null,
    turn_concrete_shift_id      bigint not null
        constraint fkt6xetpaexs3090h0gpfhk2joh
            references concrete_shift
);

alter table request
    owner to sprintfloyd;


create table request_removal_from_concrete_shift
(
    request_removal_from_concrete_shift_id bigint       not null
        primary key,
    file                                   oid,
    is_accepted                            boolean      not null,
    is_reviewed                            boolean      not null,
    reason                                 varchar(255) not null,
    concrete_shift_id                      bigint       not null
        constraint fklhfoqtrp3m12wpqu9hikc307h
            references concrete_shift,
    requesting_doctor_ms3_system_user_id   bigint       not null
        constraint fk2hai5b9f0lhlxod5va89o7kui
            references doctor,
    substitute_doctor_ms3_system_user_id   bigint
        constraint fksaco8qurl42be85an4go2drro
            references doctor
);

alter table request_removal_from_concrete_shift
    owner to sprintfloyd;

