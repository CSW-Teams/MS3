create table doctor_assignment
(
    task_id                      bigint  not null
        primary key,
    concrete_shift_doctor_status integer not null,
    concrete_shift_id            bigint  not null
        constraint fk92t3gqc4i1ycs4f95wytawixv
            references concrete_shift,
    doctor_ms3_tenant_user_id    bigint  not null
        constraint fkt0f5xuaw09r6ex1ertjogv718
            references doctor,
    task_task_id                 bigint  not null
        constraint fkibt930hatuyfynknbmesv628n
            references task
);

alter table doctor_assignment
    owner to sprintfloyd;

create table concrete_shift_doctor_assignment_list
(
    concrete_shift_concrete_shift_id bigint not null
        constraint fkrwqb43jk74ocfrh47harf14o9
            references concrete_shift,
    doctor_assignment_list_task_id   bigint not null
        constraint uk_7hu5s6b618labibw0lkmk23md
            unique
        constraint fkee3lbf3rqw3qxtg7vem64frs9
            references doctor_assignment
);

alter table concrete_shift_doctor_assignment_list
    owner to sprintfloyd;