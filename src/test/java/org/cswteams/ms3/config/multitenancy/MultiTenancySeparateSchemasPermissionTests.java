package org.cswteams.ms3.config.multitenancy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class MultiTenancySeparateSchemasPermissionTests {

    private final Map<String, DataSource> tenantDataSources = new HashMap<>();

    @Autowired
    private TenantProperties tenantProperties; // Usa TenantProperties per caricare le configurazioni

    @PostConstruct
    void setUp() {
        // Crea e aggiungi DataSource per ciascun tenant/schema usando TenantProperties
        tenantProperties.getTenants().forEach((tenantIdentifier, config) -> {
            tenantDataSources.put(tenantIdentifier, DataSourceConfig.createTenantDataSource(
                    config.getUrl(),
                    config.getUsername(),
                    config.getPassword(),
                    config.getDriver()
            ));
        });
    }

    private void executeQuery(String schema, String query) {
        DataSource dataSource = tenantDataSources.get(schema);

        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource not found for schema: " + schema);
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute(query);
    }

    @AfterEach
    void tearDown() {
        tenantDataSources.values().forEach(dataSource -> {
            try {
                if (dataSource instanceof AutoCloseable) {
                    ((AutoCloseable) dataSource).close(); // Chiudi il pool
                }
            } catch (Exception e) {
                System.err.println("Failed to close DataSource: " + e.getMessage());
            }
        });
    }

    @Test
    void testUnauthorizedAccessFails() {
        String schema = "a";
        String unauthorizedQuery = "SELECT * FROM b.concrete_shift";

        // Verifica che venga sollevata una BadSqlGrammarException
        BadSqlGrammarException exception = assertThrows(BadSqlGrammarException.class, () -> {
            executeQuery(schema, unauthorizedQuery);
        });

        // Verifica che il messaggio di errore contenga "permission denied"
        String expectedMessage = "permesso negato per lo schema b";
        assertTrue(exception.getMessage().contains(expectedMessage),
                "Expected permission denied message, but got: " + exception.getMessage());
    }

    @Test
    void testAuthorizedAccessSucceeds() {
        String schema = "a";
        String authorizedQuery = "ALTER TABLE a.doctor ADD COLUMN test_column VARCHAR(255)";

        // Verifica che l'esecuzione della query non sollevi eccezioni
        assertDoesNotThrow(() -> {
            executeQuery(schema, authorizedQuery);
        });
    }
}
