CREATE TABLE ms3_condition (
    id SERIAL PRIMARY KEY,
    type VARCHAR(255) NOT NULL);

CREATE TABLE ms3_temporary_condition (
    id SERIAL PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    start_date BIGINT NOT NULL,
    end_date BIGINT NOT NULL);