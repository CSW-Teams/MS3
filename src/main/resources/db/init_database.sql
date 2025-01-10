DO $$
BEGIN
   -- Creazione del database central_db
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'central_db') THEN
      PERFORM dblink_exec(
         'host=localhost dbname=postgres user=sprintfloyd password=sprintfloyd',
         'CREATE DATABASE central_db'
      );
   END IF;

   -- Creazione della tabella ms3_system_users nel database central_db
   PERFORM dblink_exec(
      'host=localhost dbname=central_db user=sprintfloyd password=sprintfloyd',
      'CREATE TABLE IF NOT EXISTS ms3_system_users (
          id BIGSERIAL PRIMARY KEY,
          name VARCHAR(255) NOT NULL,
          lastname VARCHAR(255) NOT NULL,
          birthday DATE NOT NULL,
          tax_code VARCHAR(255) NOT NULL,
          email VARCHAR(255) UNIQUE NOT NULL,
          password VARCHAR(255) NOT NULL,
          tenant VARCHAR(255) NOT NULL
      )'
   );

   -- Creazione del database tenant_a
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'a') THEN
      PERFORM dblink_exec(
         'host=localhost dbname=postgres user=sprintfloyd password=sprintfloyd',
         'CREATE DATABASE a'
      );
   END IF;

   -- Creazione del database tenant_b
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'b') THEN
      PERFORM dblink_exec(
         'host=localhost dbname=postgres user=sprintfloyd password=sprintfloyd',
         'CREATE DATABASE b'
      );
   END IF;

   -- Creazione della tabella nel database tenant_a
   PERFORM dblink_exec(
      'host=localhost dbname=a user=sprintfloyd password=sprintfloyd',
      'CREATE TABLE IF NOT EXISTS doctors (
          id BIGSERIAL PRIMARY KEY,
          name VARCHAR(255) NOT NULL,
          lastname VARCHAR(255) NOT NULL,
          birthday DATE NOT NULL,
          tax_code VARCHAR(255) NOT NULL,
          email VARCHAR(255) UNIQUE NOT NULL,
          password VARCHAR(255) NOT NULL
      )'
   );

   -- Creazione della tabella nel database tenant_b
   PERFORM dblink_exec(
      'host=localhost dbname=b user=sprintfloyd password=sprintfloyd',
      'CREATE TABLE IF NOT EXISTS doctors (
          id BIGSERIAL PRIMARY KEY,
          name VARCHAR(255) NOT NULL,
          lastname VARCHAR(255) NOT NULL,
          birthday DATE NOT NULL,
          tax_code VARCHAR(255) NOT NULL,
          email VARCHAR(255) UNIQUE NOT NULL,
          password VARCHAR(255) NOT NULL
      )'
   );
END $$;
