CREATE TABLE ms3_scocciatura (
    id SERIAL PRIMARY KEY,
    tipo_scocciatura VARCHAR(255) NOT NULL,
    CONSTRAINT chk_tipo_scocciatura CHECK(tipo_scocciatura IN ('ScocciaturaType1', 'ScocciaturaType2')));