package org.cswteams.ms3.config.multitenancy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component
public class DatabaseInitializer {
    @Value("${spring.datasource.superuser}")
    private String superUser;

    @Value("${spring.datasource.superpassword}")
    private String superPassword;

    @Value("${spring.datasource.url}")
    private String postgresUrl;

    @Value("${spring.datasource.username}")
    private String postgresUser;

    @Value("${spring.datasource.password}")
    private String postgresPassword;

    @Value("${spring.datasource.databases}")
    private List<String> databases;

    @Value("${spring.datasource.users}")
    private List<String> users;

    @Value("${spring.datasource.passwords}")
    private List<String> passwords;

    @Value("${spring.datasource.grant-scripts}")
    private List<String> grantScripts;

    @Value("${spring.datasource.init-public-db}")
    private String initPublicDbScript;

    @Value("${spring.datasource.init-generic-db}")
    private String initGenericDbScript;

    @PostConstruct
    public void initializeDatabases() {
        // Esegui gli script di inizializzazione sul database principale
        executeScript(postgresUrl, postgresUser, postgresPassword, "/db/terminate_connections.sql");
        executeScript(postgresUrl, postgresUser, postgresPassword, "/db/drop_and_create_databases.sql");
        executeScript(postgresUrl, postgresUser, postgresPassword, "/db/init_public_db.sql");

        // Esegui la configurazione per ogni database
        for (int i = 0; i < databases.size(); i++) {
            String dbUrl = "jdbc:postgresql://localhost:5432/" + databases.get(i);
            System.out.println("Configuring database: " + databases.get(i));

            // Esegui script di grant per il database
            executeScript(dbUrl, superUser, superPassword, grantScripts.get(i));


            // Inizializza database public
            if (i == 0) {
                executeScript(dbUrl, users.get(i), passwords.get(i), initPublicDbScript);
            }

            // Inizializza database ms3_a e ms3_b con lo script generico
            else {
                executeScript(dbUrl, users.get(i), passwords.get(i), initGenericDbScript);
            }
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
