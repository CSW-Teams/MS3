CREATE TABLE ms3_system_user (
    ms3_system_user_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL,
    tax_code VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    tenant VARCHAR(255) NOT NULL
);

CREATE TABLE system_user_system_actors (
    ms3_system_user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (ms3_system_user_id, role),
    FOREIGN KEY (ms3_system_user_id) REFERENCES ms3_system_user(ms3_system_user_id) ON DELETE CASCADE
);
