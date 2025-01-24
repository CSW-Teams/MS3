package org.cswteams.ms3.config.multitenancy;

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

    @PostConstruct
    public void initializeDatabases() {
        executeScript("jdbc:postgresql://localhost:5432/postgres", "sprintfloyd", "sprintfloyd", "/db/terminate_connections.sql");
        executeScript("jdbc:postgresql://localhost:5432/postgres", "sprintfloyd", "sprintfloyd", "/db/drop_and_create_databases.sql");
        executeScript("jdbc:postgresql://localhost:5432/postgres", "sprintfloyd", "sprintfloyd", "/db/init_databases.sql");
        executeScript("jdbc:postgresql://localhost:5432/postgres", "sprintfloyd", "sprintfloyd", "/db/create_roles.sql");

        String[] databases = {"ms3_public", "ms3_a", "ms3_b"};
        String[] users = {"sprintfloyd", "sprintfloyd", "sprintfloyd"}; // Usa sprintfloyd per eseguire gli script
        String[] passwords = {"sprintfloyd", "sprintfloyd", "sprintfloyd"};
        String[] grantScripts = {"/db/grant_privileges_ms3_public.sql", "/db/grant_privileges_ms3_a.sql", "/db/grant_privileges_ms3_b.sql"};

        for (int i = 0; i < databases.length; i++) {
            String dbUrl = "jdbc:postgresql://localhost:5432/" + databases[i];
            System.out.println("Configuring database: " + databases[i]);

            // Esegui script per assegnare privilegi specifici con sprintfloyd
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

}
