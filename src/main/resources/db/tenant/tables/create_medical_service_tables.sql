create table medical_service
(
    medical_service_id bigserial
        primary key,
    label              varchar(255) not null,
    is_deleted         boolean      not null
);


create table medical_service_tasks
(
    medical_service_medical_service_id bigint not null
        constraint fkmb680028kpvielpqvutiqkmw9
            references medical_service,
    tasks_task_id                      bigint not null
        constraint fkavku6paea867say2pcg2diihm
            references task
);
