create table schedule_feedback (
                                   id bigint not null,
                                   comment varchar(255),
                                   score integer not null,
                                   timestamp bigint not null,
                                   doctor_id bigint not null,
                                   primary key (id),
                                   constraint fk_feedback_author foreign key (doctor_id) references doctor (ms3_tenant_user_id)
);

-- Tabella di join per la relazione many-to-many
create table schedule_feedback_concrete_shifts (
                                                   schedule_feedback_id bigint not null,
                                                   concrete_shift_id bigint not null,
                                                   constraint fk_feedback_join foreign key (schedule_feedback_id) references schedule_feedback (id),
                                                   constraint fk_shift_join foreign key (concrete_shift_id) references concrete_shift (concrete_shift_id)
);