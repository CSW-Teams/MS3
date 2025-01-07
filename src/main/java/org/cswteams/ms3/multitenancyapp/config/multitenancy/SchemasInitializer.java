package org.cswteams.ms3.multitenancyapp.config.multitenancy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.annotation.PostConstruct;
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
        // Step 1: Crea le tabelle comuni nello schema 'public'
        createTablesInPublicSchema();

        // Step 2: Crea la tabella TenantUser in ciascun schema dei tenant
        for (String schema : tenantSchemas) {
            try (Connection connection = dataSource.getConnection()) {
                changeSchemaToTenant(connection, schema);
                createTenantUserTableForTenant(connection, schema);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTablesInPublicSchema() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Comando SQL per creare la tabella ms3_system_users
            String sqlSystemUser = "CREATE TABLE IF NOT EXISTS ms3_system_users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "lastname VARCHAR(255) NOT NULL, " +
                    "birthday DATE NOT NULL, " +
                    "tax_code VARCHAR(20) UNIQUE NOT NULL, " +
                    "email VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL);";

            // Comando SQL per creare la tabella ms3_hospitals
            String sqlHospital = "CREATE TABLE IF NOT EXISTS ms3_hospitals (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "address VARCHAR(255) NOT NULL);";

            // Comando SQL per creare la tabella di relazione tra ms3_system_users e ms3_hospitals
            String sqlSystemUserHospital = "CREATE TABLE IF NOT EXISTS ms3_user_hospital_mapping (" +
                    "user_id BIGINT NOT NULL, " +
                    "hospital_id BIGINT NOT NULL, " +
                    "PRIMARY KEY (user_id, hospital_id), " +
                    "FOREIGN KEY (user_id) REFERENCES ms3_system_users(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (hospital_id) REFERENCES ms3_hospitals(id) ON DELETE CASCADE" +
                    ");";

            // Esegui le query per creare le tabelle
            statement.executeUpdate(sqlSystemUser);
            statement.executeUpdate(sqlHospital);
            statement.executeUpdate(sqlSystemUserHospital);

            System.out.println("Tabelle ms3_system_users, ms3_hospitals e system_user_hospital create nello schema 'public'");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nella creazione delle tabelle nello schema public", e);
        }
    }

    private void createTenantUserTableForTenant(Connection connection, String tenantName) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            // Crea la tabella tenantUser
            String sqlTenantUser = "CREATE TABLE IF NOT EXISTS ms3_tenant_users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "lastname VARCHAR(255) NOT NULL, " +
                    "birthday DATE NOT NULL, " +
                    "tax_code VARCHAR(20) UNIQUE NOT NULL, " +
                    "email VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL);";

            // Esegui la query per creare la tabella
            statement.executeUpdate(sqlTenantUser);

            System.out.println("Tabella ms3_tenant_users creata nello schema " + tenantName);
        } catch (SQLException e) {
            System.out.println("Errore nella creazione della tabella ms3_tenant_users nello schema " + tenantName);
            throw new RuntimeException(e);
        }


    }

    private void changeSchemaToTenant(Connection connection, String tenantName) throws SQLException {
        Statement statement = connection.createStatement();

        // Passa allo schema del tenant
        statement.execute("SET search_path TO " + tenantName);
    }
}