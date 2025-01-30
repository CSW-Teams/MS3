create table waiver
(
    waiver_id bigserial
        primary key,
    data      oid,
    name      varchar(255),
    type      varchar(255)
);

