-- File: create_tenant_metadata.sql
DROP TABLE IF EXISTS tenant_metadata;
CREATE TABLE IF NOT EXISTS tenant_metadata (
    tenant_id SERIAL PRIMARY KEY,
    tenant_name VARCHAR(255) NOT NULL UNIQUE,
    database_name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
