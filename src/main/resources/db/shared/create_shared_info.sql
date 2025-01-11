create sequence hibernate_sequence;
alter sequence hibernate_sequence owner to sprintfloyd;


create table hibernate_sequences
(
    sequence_name varchar(255) not null
        primary key,
    next_val      bigint
);

alter table hibernate_sequences
    owner to sprintfloyd;