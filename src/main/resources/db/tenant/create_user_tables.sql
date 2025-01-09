CREATE TABLE ms3_tenant_user (
    ms3_tenant_user_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL,
    tax_code VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE ms3_tenant_user_system_actor (
    ms3_tenant_user_id BIGINT NOT NULL,
    system_actor VARCHAR(50) NOT NULL,
    PRIMARY KEY (ms3_tenant_user_id, system_actor),
    FOREIGN KEY (ms3_tenant_user_id) REFERENCES ms3_tenant_user(ms3_tenant_user_id) ON DELETE CASCADE
);
