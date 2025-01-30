create sequence hibernate_sequence;

create table hibernate_sequences
(
    sequence_name varchar(255) not null
        primary key,
    next_val      bigint
);