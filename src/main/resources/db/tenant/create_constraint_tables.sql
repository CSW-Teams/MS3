CREATE TABLE ms3_constraint (
    constraint_id SERIAL PRIMARY KEY,
    violable BOOLEAN NOT NULL DEFAULT FALSE,
    description VARCHAR(255) NOT NULL,
    constraint_type VARCHAR(255) NOT NULL);

CREATE TABLE ms3_config_vinc_max_per_cons (
    max_periodo_id SERIAL PRIMARY KEY,
    constrained_condition_id BIGINT NOT NULL,
    max_consecutive_minutes INT NOT NULL,
    CONSTRAINT fk_constrained_condition FOREIGN KEY (constrained_condition_id) REFERENCES ms3_condition(id));

CREATE TABLE ms3_config_vincoli (
    config_vincoli_id SERIAL PRIMARY KEY,
    period_days_no INT NOT NULL,
    period_max_time INT NOT NULL,
    horizon_night_shift INT NOT NULL,
    max_consecutive_time_for_everyone INT NOT NULL);

CREATE TABLE ms3_config_vinc_max_per_cons_per_categoria (
    config_vincoli_id BIGINT NOT NULL,
    max_periodo_id BIGINT NOT NULL,
    CONSTRAINT fk_config_vincoli FOREIGN KEY (config_vincoli_id) REFERENCES ms3_config_vincoli(config_vincoli_id),
    CONSTRAINT fk_max_periodo FOREIGN KEY (max_periodo_id) REFERENCES ms3_config_vinc_max_per_cons(max_periodo_id));

CREATE TABLE ms3_constraint_max_ore_periodo (
    id SERIAL PRIMARY KEY,
    period_duration INT NOT NULL,
    period_max_time BIGINT NOT NULL,
    constraint_assegnazione_turno_turno_id BIGINT NOT NULL,
    CONSTRAINT fk_constraint_assegnazione_turno_turno FOREIGN KEY (constraint_assegnazione_turno_turno_id)
         REFERENCES ms3_constraint_assegnazione_turno_turno(id));

CREATE TABLE ms3_constraint_max_periodo_consecutivo (
    id SERIAL PRIMARY KEY,
    max_consecutive_minutes BIGINT NOT NULL,
    constrained_category_id BIGINT,
    constraint_assegnazione_turno_turno_id BIGINT NOT NULL,
    CONSTRAINT fk_constraint_assegnazione_turno_turno FOREIGN KEY (constraint_assegnazione_turno_turno_id)
        REFERENCES ms3_constraint_assegnazione_turno_turno(id),
    CONSTRAINT fk_constrained_category FOREIGN KEY (constrained_category_id)
        REFERENCES ms3_condition(id) ON DELETE SET NULL);

CREATE TABLE ms3_constraint_turni_contigui (
    id SERIAL PRIMARY KEY,
    horizon INT NOT NULL,
    t_unit VARCHAR(255) NOT NULL,
    time_slot VARCHAR(255) NOT NULL,
    forbidden_time_slots TEXT[] NOT NULL,
    constraint_assegnazione_turno_turno_id BIGINT NOT NULL,
    CONSTRAINT fk_constraint_assegnazione_turno_turno FOREIGN KEY (constraint_assegnazione_turno_turno_id)
        REFERENCES ms3_constraint_assegnazione_turno_turno(id));

CREATE TABLE ms3_context_constraint (
    id SERIAL PRIMARY KEY,
    doctor_uffa_priority_id BIGINT NOT NULL,
    concrete_shift_id BIGINT NOT NULL,
    doctor_holidays_id BIGINT,
    holidays_ids TEXT[],
    CONSTRAINT fk_doctor_uffa_priority FOREIGN KEY (doctor_uffa_priority_id)
        REFERENCES ms3_doctor_uffa_priority(id),
    CONSTRAINT fk_concrete_shift FOREIGN KEY (concrete_shift_id)
        REFERENCES ms3_concrete_shift(id),
    CONSTRAINT fk_doctor_holidays FOREIGN KEY (doctor_holidays_id)
        REFERENCES ms3_doctor_holidays(id));