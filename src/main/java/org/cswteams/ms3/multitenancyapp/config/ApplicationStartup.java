package org.cswteams.ms3.multitenancyapp.config;

import lombok.SneakyThrows;
import org.cswteams.ms3.multitenancyapp.entity.*;
import org.cswteams.ms3.multitenancyapp.dao.*;
import org.cswteams.ms3.multitenancyapp.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.util.*;

@Component()
@Profile("!test")
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */

    @Autowired
    private HospitalDAO hospitalDAO;

    @Autowired
    private SystemUserDAO systemUserDAO;

    @Autowired
    private TenantUserDAO tenantUserDAO;


    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        /**
         * FIXME: sostiutire count con controllo su entità Config
         */
         // if (userDAO.count() == 0) {

        // Creare schemi specifici per i tenant
            //createSchemaForTenant("tenant_a");
            //createSchemaForTenant("tenant_b");
            //createTables();

            populateDB();

            // Ripristina lo schema di default
            //switchToTenantSchema("public");
         // }

    }

    /*private void createTables() {
        // Step 1: Crea le tabelle comuni nello schema 'public'
        createTablesInPublicSchema();

        // Step 2: Crea la tabella TenantUser in ciascun schema dei tenant (tenant_a, tenant_b)
        createTenantUserTableForTenant("tenant_a");
        createTenantUserTableForTenant("tenant_b");
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

    private void createTenantUserTableForTenant(String tenantName) {
        switchToTenantSchema(tenantName);
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Passa allo schema del tenant
            statement.execute("SET search_path TO "+ tenantName);

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
            e.printStackTrace();
            throw new RuntimeException("Errore nella creazione della tabella ms3_tenant_users per il tenant " + tenantName, e);
        }
    }

    /**
     * Crea uno schema per il tenant specificato.
     *
     * @param tenantName Il nome del tenant (schema da creare).
     */
    // Crea lo schema per il tenant specificato
    /*private void createSchemaForTenant(String tenantName) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Comando SQL per creare il nuovo schema
            String sql = "CREATE SCHEMA IF NOT EXISTS " + tenantName;
            statement.executeUpdate(sql);

            System.out.println("Schema " + tenantName + " created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating schema: " + tenantName, e);
        }
    }*/

    /*/**
     * Esegui il cambio dello schema per il tenant tramite Hibernate
     *
     * @param tenantSchema Il nome del tenant (schema da selezionare)
     */
    /*private void switchToTenantSchema(String tenantSchema) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Passa allo schema del tenant
            statement.execute("SET search_path TO " + tenantSchema);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error connecting to schema: " + tenantSchema, e);
        }
    }*/

    private void addUserToTenantsSelective(Map<String, Hospital> tenantHospitalMap, SystemUser user) {
        for (Map.Entry<String, Hospital> entry : tenantHospitalMap.entrySet()) {
            String tenantSchema = entry.getKey();

            try {
                // Passa allo schema specifico del tenant
                //switchToTenantSchema(tenantSchema);
                //printCurrentSchema();

                // Clona l'utente con solo l'ospedale specifico di questo tenant
                TenantUser tenantSpecificUser = new TenantUser(
                        user.getName(),
                        user.getLastname(),
                        user.getTaxCode(),
                        user.getBirthday(),
                        user.getEmail(),
                        user.getPassword()
                );

                // Salva l'utente nello schema del tenant
                TenantContext.setCurrentTenant(tenantSchema);
                tenantUserDAO.saveAndFlush(tenantSpecificUser);
            } catch (Exception e) {
                System.err.println("Errore nell'inserimento utente nello schema " + tenantSchema + ": " + e.getMessage());
            }
        }
    }

    /*private void printCurrentSchema() {
        try {
            String currentSchema = jdbcTemplate.queryForObject("SELECT current_schema()", String.class);
            System.out.println("Current schema: " + currentSchema); // Stampa lo schema corrente
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void populateDB() {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Hospital h1 = new Hospital("A", "Piazza di Sant'Onofrio, 4, RM");
        Hospital h2 = new Hospital("B", "Largo Agostino Gemelli, 8, RM");

        h1 = hospitalDAO.saveAndFlush(h1);
        h2 = hospitalDAO.saveAndFlush(h2);

        SystemUser u3 = new SystemUser("Federica", "Villani", "VLLFRC98P43H926Y", LocalDate.of(1998, 9, 3), "federicavillani@gmail.com", encoder.encode("passw"), Set.of(h1));
        SystemUser u4 = new SystemUser("Daniele", "Colavecchi", "CLVDNL82C21H501E", LocalDate.of(1982, 7, 6), "danielecolavecchi@gmail.com", encoder.encode("passw"), Set.of(h2));
        SystemUser u5 = new SystemUser("Daniele", "La Prova", "LPRDNL98H13H501F", LocalDate.of(1998, 2, 12), "danielelaprova@gmail.com", encoder.encode("passw"), Set.of(h1));
        SystemUser u7 = new SystemUser("Luca", "Fiscariello", "FSCLCU99D15A783Z", LocalDate.of(1998, 8, 12), "lucafiscariello@gmail.com", encoder.encode("passw"), Set.of(h2));
        SystemUser u8 = new SystemUser("Manuel", "Mastrofini", "MSTMNL80M20H501X", LocalDate.of(1988, 5, 4), "manuelmastrofini@gmail.com", encoder.encode("passw"), Set.of(h1, h2));
        SystemUser u10 = new SystemUser("Fabio", "Valenzi", "VLZFBA90A03H501U", LocalDate.of(1989, 12, 6), "fabiovalenzi@gmail.com", encoder.encode("passw"), Set.of(h1, h2));

        // Salvare gli utenti nel database globale (schema `public`)
        u3 = systemUserDAO.saveAndFlush(u3);
        u4 = systemUserDAO.saveAndFlush(u4);
        u5 = systemUserDAO.saveAndFlush(u5);
        u7 = systemUserDAO.saveAndFlush(u7);
        u8 = systemUserDAO.saveAndFlush(u8);
        u10 = systemUserDAO.saveAndFlush(u10);

        SystemUser u9 = new SystemUser("Giulia", "Cantone II", "CTNGLI78E44H501Z", LocalDate.of(1991, 2, 12), "giuliacantone@gmail.com", encoder.encode("passw"), Set.of(h1));
        SystemUser u1 = new SystemUser("Martina", "Salvati", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "salvatimartina97@gmail.com", encoder.encode("passw"), Set.of(h1, h2));
        SystemUser u2 = new SystemUser("Domenico", "Verde", "VRDDMC96H16H501H", LocalDate.of(1997, 5, 23), "domenicoverde@gmail.com", encoder.encode("passw"), Set.of(h2));
        SystemUser u6 = new SystemUser("Giovanni", "Cantone", "GVNCTN48M22D429G", LocalDate.of(1960, 3, 7), "giovannicantone@gmail.com", encoder.encode("passw"), Set.of(h1, h2));
        SystemUser u44 = new SystemUser("Giulio","Farnasini","GLIFNS94M07G224O",LocalDate.of(1994,8,7),"giuliofarnasini@gmail.com",encoder.encode("passw"), Set.of(h1, h2));
        SystemUser u45 = new SystemUser("Full","Permessi","FLLPRM98M24G224O",LocalDate.of(1998,8,24),"fullpermessi@gmail.com",encoder.encode("passw"), Set.of(h1, h2));

        u1 = systemUserDAO.saveAndFlush(u1);
        u2 = systemUserDAO.saveAndFlush(u2);
        u6 = systemUserDAO.saveAndFlush(u6);
        u9 = systemUserDAO.saveAndFlush(u9);
        u44 = systemUserDAO.saveAndFlush(u44);
        u45 = systemUserDAO.saveAndFlush(u45);

        // Mappa tenant -> ospedale relativo
        Map<String, Hospital> tenantHospitalMap = Map.of(
                "tenant_a", h1,
                "tenant_b", h2
        );

        // Inserire utenti negli schemi tenant
        addUserToTenantsSelective(tenantHospitalMap, u1);
        addUserToTenantsSelective(tenantHospitalMap, u2);
        addUserToTenantsSelective(tenantHospitalMap, u3);
        addUserToTenantsSelective(tenantHospitalMap, u4);
        addUserToTenantsSelective(tenantHospitalMap, u5);
        addUserToTenantsSelective(tenantHospitalMap, u6);
        addUserToTenantsSelective(tenantHospitalMap, u7);
        addUserToTenantsSelective(tenantHospitalMap, u8);
        addUserToTenantsSelective(tenantHospitalMap, u9);
        addUserToTenantsSelective(tenantHospitalMap, u10);
        addUserToTenantsSelective(tenantHospitalMap, u44);
        addUserToTenantsSelective(tenantHospitalMap, u45);

    }

}
