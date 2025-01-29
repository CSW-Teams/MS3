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
    @Value("${spring.datasource.url}")
    private String postgresUrl;

    @Value("${spring.datasource.tenant.public.url}")
    private String defaultUrl;

    @Value("${spring.datasource.username}")
    private String postgresUser;

    @Value("${spring.datasource.password}")
    private String postgresPassword;

    @Value("${spring.datasource.tenant.public.name}")
    private String publicDatabase;

    @Value("${spring.datasource.tenant.a.name}")
    private String tenantADatabase;

    @Value("${spring.datasource.tenant.b.name}")
    private String tenantBDatabase;

    @Value("${spring.datasource.roles.public.username}")
    private String publicRoleName;

    @Value("${spring.datasource.roles.a.username}")
    private String tenantARoleName;

    @Value("${spring.datasource.roles.b.username}")
    private String tenantBRoleName;

    @Value("${spring.datasource.roles.public.password}")
    private String publicPassword;

    @Value("${spring.datasource.roles.a.password}")
    private String tenantAPassword;

    @Value("${spring.datasource.roles.b.password}")
    private String tenantBPassword;

    @Value("${spring.datasource.scripts.privileges.public}")
    private String grantPrivilegesPublic;

    @Value("${spring.datasource.scripts.privileges.a}")
    private String grantPrivilegesTenantA;

    @Value("${spring.datasource.scripts.privileges.b}")
    private String grantPrivilegesTenantB;

    @Value("${spring.datasource.scripts.initialize.public}")
    private String initializePublicDB;

    @Value("${spring.datasource.scripts.initialize.a}")
    private String initializeTenantA;

    @Value("${spring.datasource.scripts.initialize.b}")
    private String initializeTenantB;



    @PostConstruct
    public void initializeDatabases() {
        // Esegui la configurazione per ogni database
        String dbPublicUrl = defaultUrl + publicDatabase;
        String dbTenantAUrl = defaultUrl + tenantADatabase;
        String dbTenantBUrl = defaultUrl + tenantBDatabase;

        // Esegui gli script di inizializzazione sul database principale
        executeScript(postgresUrl, postgresUser, postgresPassword, "/db/terminate_connections.sql");
        executeScript(postgresUrl, postgresUser, postgresPassword, "/db/drop_and_create_databases.sql");

        // Esegui script di grant per il database
        executeScript(dbPublicUrl, postgresUser, postgresPassword, grantPrivilegesPublic);
        executeScript(dbTenantAUrl, postgresUser, postgresPassword, grantPrivilegesTenantA);
        executeScript(dbTenantBUrl, postgresUser, postgresPassword, grantPrivilegesTenantB);

        executeScript(dbPublicUrl, publicRoleName, publicPassword, initializePublicDB);
        executeScript(dbTenantAUrl, tenantARoleName, tenantAPassword, initializeTenantA);
        executeScript(dbTenantBUrl, tenantBRoleName, tenantBPassword, initializeTenantB);

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
            //System.out.println("Script " + scriptPath + " eseguito con successo su " + dbUrl);

        } catch (Exception e) {
            System.err.println("Errore nell'esecuzione dello script " + scriptPath + " su " + dbUrl + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
