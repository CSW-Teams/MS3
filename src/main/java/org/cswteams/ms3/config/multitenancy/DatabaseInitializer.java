package org.cswteams.ms3.config.multitenancy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Objects;

@Component
public class DatabaseInitializer {

    @Value("${spring.datasource.url}")
    private String postgresUrl;

    @Value("${spring.datasource.username}")
    private String postgresUser;

    @Value("${spring.datasource.password}")
    private String postgresPassword;

    @Value("${spring.datasource.databases}")
    private String[] databases;

    @Value("${spring.datasource.users}")
    private String[] users;

    @Value("${spring.datasource.passwords}")
    private String[] passwords;

    @Value("${spring.datasource.grant-scripts}")
    private String[] grantScripts;

    @PostConstruct
    public void initializeDatabases() {
        executeScript(postgresUrl, postgresUser, postgresPassword, "/db/terminate_connections.sql");
        executeScript(postgresUrl, postgresUser, postgresPassword, "/db/drop_and_create_databases.sql");

        for (int i = 0; i < databases.length; i++) {
            String dbUrl = "jdbc:postgresql://localhost:5432/" + databases[i];
            System.out.println("Enabling dblink for database : " + databases[i]);

            // Esegui script per assegnare privilegi specifici
            enableDblink(dbUrl, users[i], passwords[i]);
        }

        executeScript(postgresUrl, postgresUser, postgresPassword, "/db/init_databases.sql");
        executeScript(postgresUrl, postgresUser, postgresPassword, "/db/create_roles.sql");

        for (int i = 0; i < databases.length; i++) {
            String dbUrl = "jdbc:postgresql://localhost:5432/" + databases[i];
            System.out.println("Configuring database: " + databases[i]);

            // Esegui script per assegnare privilegi specifici
            executeScript(dbUrl, users[i], passwords[i], grantScripts[i]);
        }
    }

    private void executeScript(String dbUrl, String user, String password, String scriptPath) {
        try (Connection connection = DriverManager.getConnection(dbUrl, user, password);
             Statement statement = connection.createStatement()) {

            StringBuilder sqlScript = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(scriptPath))))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    sqlScript.append(line).append("\n");
                }
            }

            statement.execute(sqlScript.toString());
            System.out.println("Script " + scriptPath + " eseguito con successo su " + dbUrl);

        } catch (Exception e) {
            System.err.println("Errore nell'esecuzione dello script " + scriptPath + " su " + dbUrl + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void enableDblink(String dbUrl, String user, String password) {
        try (Connection connection = DriverManager.getConnection(dbUrl, user, password);
             Statement statement = connection.createStatement()) {

            statement.execute("CREATE EXTENSION IF NOT EXISTS dblink");
            System.out.println("Estensione dblink abilitata per il database: " + dbUrl);

        } catch (Exception e) {
            System.err.println("Errore durante l'abilitazione di dblink su " + dbUrl + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
