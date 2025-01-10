create table giustificazione_forzatura_vincoli
(
    id                                   bigserial
        primary key,
    date                                 date,
    reason                               varchar(255),
    violating_shift                      integer,
    justifying_doctor_ms3_system_user_id bigint
        constraint fk76rj2ja0imtdmu5cgg03may6j
            references doctor,
    service_medical_service_id           bigint
        constraint fkoh6v3mpdpuw17nb4l31rulm45
            references medical_service
);

alter table giustificazione_forzatura_vincoli
    owner to sprintfloyd;


create table giustificazione_forzatura_vincoli_assigned_doctors
(
    giustificazione_forzatura_vincoli_id bigint not null
        constraint fk46nas32dr9swlgul9veq62hqu
            references giustificazione_forzatura_vincoli,
    assigned_doctors_ms3_system_user_id  bigint not null
        constraint fk73ysyboa2cbsge8lwj7h0eauo
            references doctor,
    primary key (giustificazione_forzatura_vincoli_id, assigned_doctors_ms3_system_user_id)
);

alter table giustificazione_forzatura_vincoli_assigned_doctors
    owner to sprintfloyd;

