package org.cswteams.ms3.config.multitenancy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class SchemasInitializer {
    private static final String DEFAULT_SCHEMA = "public";

    private final List<String> tenantSchemas;

    @Autowired
    private DataSource dataSource;

    public SchemasInitializer() throws IOException {
        // Caricare il file JSON
        ObjectMapper objectMapper = new ObjectMapper();
        TenantConfig tenantConfig = objectMapper.readValue(new File("src/main/resources/tenants_config.json"), TenantConfig.class);
        this.tenantSchemas = tenantConfig.getTenants();
    }

    @PostConstruct
    public void init() throws IOException, SQLException {
        // Carica lo script di pulizia dello schema di default dal file .sql
        ClassPathResource schemaScript1 = new ClassPathResource("db/clean_default_schema.sql");

        // Carica lo script di creazione degli schemi dal file .sql
        ClassPathResource schemaScript2 = new ClassPathResource("db/create_schemas.sql");

        // Usa la connessione dal DataSource
        try (Connection connection = dataSource.getConnection()) {
            // Esegui gli script SQL
            ScriptUtils.executeSqlScript(connection, schemaScript1);
            ScriptUtils.executeSqlScript(connection, schemaScript2);

            // Crea le tabelle nei vari schemi
            createTables();

            // Ritorna allo schema di default
            changeSchemaToTenant(connection, DEFAULT_SCHEMA);
        } catch (SQLException e) {
            // Gestione errori durante l'esecuzione
            e.printStackTrace();
            throw new SQLException("Errore durante l'esecuzione dello script di creazione degli schemi", e);
        }
    }

    private void createTables() {
        try (Connection connection = dataSource.getConnection()) {
            // Step 1: Crea la tabella comune nello schema 'public'
            createTablesInPublicSchema(connection);

            // Step 2: Crea le varie tabelle in ciascun schema dei tenant
            for (String schema : tenantSchemas) {
                changeSchemaToTenant(connection, schema.toLowerCase());
                createTablesForTenant(connection, schema.toLowerCase());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTablesInPublicSchema(Connection connection) {
        ClassPathResource tableScript;
        try {
            tableScript = new ClassPathResource("db/create_system_user_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            System.out.println("Tabella ms3_system_users creata nello schema 'public'");
        } catch (ScriptException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nella creazione della tabella nello schema public", e);
        }
    }

    private void createTablesForTenant(Connection connection, String tenantName) {
        ClassPathResource tableScript;
        try {
            tableScript = new ClassPathResource("db/tenant/create_user_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            /*tableScript = new ClassPathResource("db/tenant/create_condition_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/create_constraint_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/create_scocciature_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);*/


            List<String> tableCreationQueries = List.of(
                    "CREATE TABLE IF NOT EXISTS ms3_tenant_users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "lastname VARCHAR(255) NOT NULL, " +
                    "birthday DATE NOT NULL, " +
                    "tax_code VARCHAR(20) UNIQUE NOT NULL, " +
                    "email VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL);",

                    "CREATE TABLE IF NOT EXISTS task (" +
                            "task_id SERIAL PRIMARY KEY, " +
                            "task_type VARCHAR(255) NOT NULL);",

                    "CREATE TABLE IF NOT EXISTS medical_service (" +
                            "medical_service_id SERIAL PRIMARY KEY, " +
                            "label VARCHAR(255) NOT NULL);",

                    "CREATE TABLE IF NOT EXISTS medical_service_tasks (" +
                            "medical_service_id BIGINT NOT NULL REFERENCES medical_service(medical_service_id) ON DELETE CASCADE, " +
                            "task_id BIGINT NOT NULL REFERENCES task(task_id) ON DELETE CASCADE, " +
                            "PRIMARY KEY (medical_service_id, task_id));",

                    "CREATE TABLE IF NOT EXISTS quantity_shift_seniority (" +
                            "id SERIAL PRIMARY KEY, " +
                            "seniority_map BYTEA, " +
                            "task_id BIGINT REFERENCES task(task_id) ON DELETE CASCADE);",

                    "CREATE TABLE IF NOT EXISTS ms3_shift (" +
                            "shift_id SERIAL PRIMARY KEY, " +
                            "time_slot VARCHAR(255) NOT NULL, " +
                            "start_time TIME NOT NULL, " +
                            "duration INTERVAL NOT NULL, " +
                            "days_of_week TEXT[], " +
                            "medical_service_id BIGINT, " +
                            "CONSTRAINT fk_medical_service FOREIGN KEY (medical_service_id) REFERENCES ms3_medical_service(medical_service_id));",

                    "CREATE TABLE IF NOT EXISTS ms3_shift_quantity_seniority (" +
                            "shift_id BIGINT NOT NULL, " +
                            "quantity_shift_seniority_id BIGINT NOT NULL, " +
                            "PRIMARY KEY (shift_id, quantity_shift_seniority_id), " +
                            "CONSTRAINT fk_shift FOREIGN KEY (shift_id) REFERENCES ms3_shift(shift_id), " +
                            "CONSTRAINT fk_quantity_shift_seniority FOREIGN KEY (quantity_shift_seniority_id) REFERENCES ms3_quantity_shift_seniority(id));",

                    "CREATE TABLE IF NOT EXISTS ms3_shift_additional_constraint (" +
                            "shift_id BIGINT NOT NULL, " +
                            "additional_constraint_id BIGINT NOT NULL, " +
                            "PRIMARY KEY (shift_id, additional_constraint_id), " +
                            "CONSTRAINT fk_shift_constraint FOREIGN KEY (shift_id) REFERENCES ms3_shift(shift_id), " +
                            "CONSTRAINT fk_additional_constraint FOREIGN KEY (additional_constraint_id) REFERENCES ms3_constraint(constraint_id));",

                    "CREATE TABLE IF NOT EXISTS violated_constraint_log_entry (" +
                            "id SERIAL PRIMARY KEY, " +
                            "violation BYTEA);",

                    "CREATE TABLE IF NOT EXISTS waiver (" +
                            "waiver_id SERIAL PRIMARY KEY, " +
                            "name VARCHAR(255), " +
                            "type VARCHAR(255), " +
                            "data BYTEA);"

            );
        } catch (ScriptException e) {
            System.out.println("Errore nella creazione delle tabelle nello schema " + tenantName);
            throw new RuntimeException("Errore nella creazione delle tabelle per il tenant " + tenantName, e);
        }
    }

    private void changeSchemaToTenant(Connection connection, String tenantName) throws SQLException {
        Statement statement = connection.createStatement();

        // Passa allo schema del tenant
        statement.execute("SET search_path TO " + tenantName);
    }
}
