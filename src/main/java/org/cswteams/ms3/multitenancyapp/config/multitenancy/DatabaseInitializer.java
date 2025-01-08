package org.cswteams.ms3.multitenancyapp.config.multitenancy;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseInitializer {

    private final DataSource dataSource;

    // Lista dei tenant
    private final List<String> tenants = Arrays.asList("tenant_a", "tenant_b");

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Inizializza il database "public" e crea la tabella tenant_metadata
            initializePublicDatabase(connection);

            // Per ogni tenant, crea il database e registra i metadati
            for (String tenant : tenants) {
                recreateDatabase(statement, tenant);
                registerTenantMetadata(connection, tenant);
            }
        }
    }

    private void initializePublicDatabase(Connection connection) throws SQLException {
        // Carica e esegui lo script SQL dal classpath
        ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/create_tenant_metadata.sql"));
        System.out.println("Tabella tenant_metadata creata (o già esistente) nel database 'public'.");
    }

    private void recreateDatabase(Statement statement, String tenant) throws SQLException {
        // Termina tutte le connessioni attive al database
        String terminateConnections =
                "SELECT pg_terminate_backend(pg_stat_activity.pid) " +
                        "FROM pg_stat_activity " +
                        "WHERE pg_stat_activity.datname = '" + tenant + "' " +
                        "AND pid <> pg_backend_pid();";

        statement.execute(terminateConnections);

        // Elimina il database esistente
        statement.execute("DROP DATABASE IF EXISTS " + tenant);

        // Crea il nuovo database
        statement.execute("CREATE DATABASE " + tenant);
        System.out.println("Database " + tenant + " ricreato con successo.");
    }

    private void registerTenantMetadata(Connection connection, String tenant) throws SQLException {
        // Registra i metadati del tenant nel database "public"
        String insertMetadata = "INSERT INTO tenant_metadata (tenant_name, database_name) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertMetadata)) {
            preparedStatement.setString(1, tenant);
            preparedStatement.setString(2, tenant);
            preparedStatement.executeUpdate();
            System.out.println("Metadati per il tenant " + tenant + " registrati con successo.");
        }
    }
}
