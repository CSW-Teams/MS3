create table doctor
(
    ms3_tenant_user_id bigint       not null
        primary key,
    birthday           date         not null,
    email              varchar(255) not null,
    lastname           varchar(255) not null,
    name               varchar(255) not null,
    password           varchar(255) not null,
    tax_code           varchar(255) not null,
    seniority          integer      not null
);


create table doctor_holidays
(
    doctor_holidays_id        bigint not null
        primary key,
    holiday_map               oid    not null,
    doctor_ms3_tenant_user_id bigint not null
        constraint fkfvgbw7dtyh2udi5gt75bbkmtl
            references doctor
);