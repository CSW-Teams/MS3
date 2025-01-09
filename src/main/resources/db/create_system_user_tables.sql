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

CREATE TABLE systemuser_systemactors (
    systemuser_ms3_system_user_id BIGINT NOT NULL,
    systemactors VARCHAR(50) NOT NULL,
    PRIMARY KEY (systemuser_ms3_system_user_id, systemactors),
    FOREIGN KEY (systemuser_ms3_system_user_id) REFERENCES ms3_system_user(ms3_system_user_id) ON DELETE CASCADE
);
