create table doctor_permanent_conditions
(
    doctor_ms3_tenant_user_id bigint not null
        constraint fk1qa08xedirepqn7v573whdh2d
            references doctor,
    permanent_conditions_id   bigint not null
        constraint fkbewktrk61ubphcu7cnegu2y3b
            references permanent_condition
);

alter table doctor_permanent_conditions
    owner to sprintfloyd;


create table doctor_preference_list
(
    doctor_ms3_tenant_user_id bigint not null
        constraint fkrftrnnnu2e47wgdk4dkcoqgia
            references doctor,
    preference_list_id        bigint not null
        constraint fkigigf535imqq5mubfmfjonalu
            references preference
);

alter table doctor_preference_list
    owner to sprintfloyd;


create table doctor_specializations
(
    doctor_ms3_tenant_user_id         bigint not null
        constraint fkrp1pvfw6amwvi3xdd176lc2tk
            references doctor,
    specializations_specialization_id bigint not null
        constraint fk9gld6lk88tvf6o3dirub3i90
            references specialization
);

alter table doctor_specializations
    owner to sprintfloyd;


create table doctor_temporary_conditions
(
    doctor_ms3_tenant_user_id bigint not null
        constraint fk2r8hut52kv1k65vjxn4732nyl
            references doctor,
    temporary_conditions_id   bigint not null
        constraint fkhvhnnfsoeadi43aihyft35wj1
            references temporary_condition
);

alter table doctor_temporary_conditions
    owner to sprintfloyd;


create table doctor_uffa_priority
(
    id                          bigint  not null
        primary key,
    general_priority            integer not null,
    long_shift_priority         integer not null,
    night_priority              integer not null,
    partial_general_priority    integer not null,
    partial_long_shift_priority integer not null,
    partial_night_priority      integer not null,
    doctor_ms3_tenant_user_id   bigint  not null
        constraint fk4nankuxa09wc9qh4grgaig502
            references doctor,
    schedule_schedule_id        bigint
        constraint fkov3bg2k966dirumhcl7oji82r
            references schedule
);

alter table doctor_uffa_priority
    owner to sprintfloyd;


create table doctor_uffa_priority_snapshot
(
    id                        bigint  not null
        primary key,
    general_priority          integer not null,
    long_shift_priority       integer not null,
    night_priority            integer not null,
    doctor_ms3_tenant_user_id bigint  not null
        constraint fkbcsgo0mtncu0vwgxvo5s731eh
            references doctor,
    schedule_schedule_id      bigint
        constraint fkex1084c0m80pdp0bsm16lk35b
            references schedule
);

alter table doctor_uffa_priority_snapshot
    owner to sprintfloyd;

