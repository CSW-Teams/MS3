create table shift
(
    shift_id                           bigint  not null
        primary key,
    duration                           bigint  not null,
    start_time                         time    not null,
    time_slot                          integer not null,
    medical_service_medical_service_id bigint
        constraint fk4vf2jsq4n82b7upj2l6ct8lpt
            references medical_service
);


create table shift_additional_constraints
(
    shift_shift_id                       bigint not null
        constraint fkmumt09206y7o4jk16qur6ebru
            references shift,
    additional_constraints_constraint_id bigint not null
        constraint fk25gyof9uyb6hiohf3ex982a6i
            references ms3_constraint
);


create table shift_days_of_week
(
    shift_shift_id bigint not null
        constraint fk9jk9s9ls1iwl5qu582p6nu9ug
            references shift,
    days_of_week   integer
);


create table shift_quantity_shift_seniority
(
    shift_shift_id              bigint not null
        constraint fk147xktaiif9ueng5l2oklby27
            references shift,
    quantity_shift_seniority_id bigint not null
        constraint uk_657e2opyl1mwcjii8a8qdmn9y
            unique
        constraint fkoamnh1j4jcb2vn4r8ftn2ondh
            references quantity_shift_seniority
);

