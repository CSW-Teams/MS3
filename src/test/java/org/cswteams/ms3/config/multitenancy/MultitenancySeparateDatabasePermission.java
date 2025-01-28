package org.cswteams.ms3.config.multitenancy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MultiTenancySeparateDatabasesPermissionTests {

    private Connection createConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Test
    void testUnauthorizedAccessFails() {
        String url = "jdbc:postgresql://localhost:5432/ms3_a";
        String user = "user_ms3_b"; // Utente non autorizzato per ms3_b
        String password = "password_b";

        Exception exception = assertThrows(SQLException.class, () -> {
            try (Connection connection = createConnection(url, user, password);
                 Statement statement = connection.createStatement()) {

                String query = "SELECT * FROM doctor"; // Query sul database ms3_b
                statement.execute(query);
            }
        });

        // Controlla che l'errore riguardi il permesso negato
        String expectedMessage = "permesso negato"; // Parte del messaggio di errore effettivo
        assertTrue(exception.getMessage().toLowerCase().contains(expectedMessage),
                "Il test ha fallito: ci si aspettava un errore di permesso negato, ma si è ricevuto: " + exception.getMessage());
    }


    @Test
    void testAuthorizedAccessSucceeds() {
        String url = "jdbc:postgresql://localhost:5432/ms3_a";
        String user = "user_ms3_a"; // Utente autorizzato per ms3_b
        String password = "password_a";

        // Verifica che l'accesso avvenga senza eccezioni
        assertDoesNotThrow(() -> {
            try (Connection connection = createConnection(url, user, password);
                 Statement statement = connection.createStatement()) {

                String query = "SELECT * FROM doctor"; // Query sul database ms3_b
                statement.execute(query);
            }
        }, "Il test ha fallito: l'accesso autorizzato ha generato un'eccezione.");
    }

}