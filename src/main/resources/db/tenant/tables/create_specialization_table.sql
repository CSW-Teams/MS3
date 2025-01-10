create table specialization
(
    specialization_id bigserial
        primary key,
    type              varchar(255) not null
);

alter table specialization
    owner to sprintfloyd;

