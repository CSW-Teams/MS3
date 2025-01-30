package org.cswteams.ms3.config.multitenancy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
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

        // Carica lo script di assegnamento dei privilegi dal file .sql
        ClassPathResource schemaScript3 = new ClassPathResource("db/assign_privileges.sql");

        // Usa la connessione dal DataSource
        try (Connection connection = dataSource.getConnection()) {
            // Esegui gli script SQL
            ScriptUtils.executeSqlScript(connection, schemaScript1);
            ScriptUtils.executeSqlScript(connection, schemaScript2);

            // Crea le tabelle nei vari schemi
            createTables();

            // Assegna tutti i privilegi corretti agli utenti del db
            ScriptUtils.executeSqlScript(connection, new EncodedResource(schemaScript3, "UTF-8"), false, false, ScriptUtils.DEFAULT_COMMENT_PREFIX, ";;",
                    ScriptUtils.DEFAULT_BLOCK_COMMENT_START_DELIMITER, ScriptUtils.DEFAULT_BLOCK_COMMENT_END_DELIMITER);

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

            tableScript = new ClassPathResource("db/shared/create_shared_info.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            System.out.println("Tabelle create nello schema 'public'");
        } catch (ScriptException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nella creazione delle tabelle nello schema public", e);
        }
    }

    private void createTablesForTenant(Connection connection, String tenantName) {
        ClassPathResource tableScript;
        try {
            tableScript = new ClassPathResource("db/shared/create_shared_info.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_tenant_user_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_doctor_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_task_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_medical_service_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_condition_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_constraint_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_quantity_shift_seniority_table.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_shift_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_preference_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_specialization_table.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_concrete_shift_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_schedule_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_other_doctor_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_doctor_assignment_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_config_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_giustificazione_forzatura_vincoli_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_holiday_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_notification_table.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_request_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_scocciatura_table.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/tables/create_waiver_table.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/sequences/create_sequence.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            System.out.println("Tabelle create nello schema " + tenantName);
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
