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

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "sprintfloyd";
    private static final String DB_PASSWORD = "sprintfloyd";

    @PostConstruct
    public void initializeDatabases() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            // Carica lo script SQL dal file
            StringBuilder sqlScript = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/db/init_databases.sql"))))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    sqlScript.append(line).append("\n");
                }
            }

            // Esegui lo script SQL
            statement.execute(sqlScript.toString());
            System.out.println("Databases initialized successfully.");

        } catch (Exception e) {
            System.err.println("Error initializing databases: " + e.getMessage());
            e.printStackTrace();
        }
    }
}