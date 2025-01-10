create table holiday
(
    id                   bigint       not null
        primary key,
    category             integer      not null,
    custom               boolean      not null,
    end_date_epoch_day   bigint       not null,
    location             varchar(255),
    name                 varchar(255) not null,
    start_date_epoch_day bigint       not null
);

alter table holiday
    owner to sprintfloyd;


create table recurrent_holiday
(
    id          bigint       not null
        primary key,
    category    integer      not null,
    end_day     integer      not null,
    end_month   integer      not null,
    location    varchar(255),
    name        varchar(255) not null,
    start_day   integer      not null,
    start_month integer      not null
);

alter table recurrent_holiday
    owner to sprintfloyd;

