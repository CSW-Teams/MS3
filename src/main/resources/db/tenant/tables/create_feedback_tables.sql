-- Purpose: store doctor feedback attached to generated schedules and assigned shifts.
-- Context: executed inside each tenant schema during tenant table bootstrap.
-- Order: run after doctor/concrete_shift tables and before tenant sequence ownership grants.
create table schedule_feedback (
                                   id bigint not null,
                                   comment varchar(255),
                                   score integer not null,
                                   timestamp bigint not null,
                                   -- Feedback author is tenant-scoped doctor identity.
                                   doctor_id bigint not null,
                                   primary key (id),
                                   constraint fk_feedback_author foreign key (doctor_id) references doctor (ms3_tenant_user_id)
);

-- Join table keeps many-to-many relation between feedback entry and evaluated shifts.
create table schedule_feedback_concrete_shifts (
                                                   schedule_feedback_id bigint not null,
                                                   concrete_shift_id bigint not null,
                                                   constraint fk_feedback_join foreign key (schedule_feedback_id) references schedule_feedback (id),
                                                   constraint fk_shift_join foreign key (concrete_shift_id) references concrete_shift (concrete_shift_id)
);
