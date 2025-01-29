DO $$
BEGIN
CREATE TABLE IF NOT EXISTS ms3_system_user (
    ms3_system_user_id bigint       not null
    primary key,
    birthday           date         not null,
    email              varchar(255) not null,
    lastname           varchar(255) not null,
    name               varchar(255) not null,
    password           varchar(255) not null,
    tax_code           varchar(255) not null,
    tenant             varchar(255) not null
);

CREATE TABLE IF NOT EXISTS ms3_system_users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL,
    tax_code VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    tenant VARCHAR(255) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence;
CREATE SEQUENCE IF NOT EXISTS constraint_id_seq increment by 50;

CREATE TABLE IF NOT EXISTS hibernate_sequences (
                  sequence_name varchar(255) not null
                          primary key,
                      next_val      bigint
              );

CREATE TABLE IF NOT EXISTS user_system_actors(
                  user_ms3_system_user_id bigint not null,
                      system_actors           integer
              );
CREATE TABLE IF NOT EXISTS systemuser_systemactors(
                  ms3_system_user_id bigint      not null,
                      role               varchar(50) not null,
                      primary key (ms3_system_user_id, role)
              );

END $$;