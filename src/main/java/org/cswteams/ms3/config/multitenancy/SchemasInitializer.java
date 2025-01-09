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
            tableScript = new ClassPathResource("db/create_system_user_table.sql");
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
            /*tableScript = new ClassPathResource("db/tenant/create_user_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/create_condition_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/create_constraint_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);

            tableScript = new ClassPathResource("db/tenant/create_scocciature_tables.sql");
            ScriptUtils.executeSqlScript(connection, tableScript);*/

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
