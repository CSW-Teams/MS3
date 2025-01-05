package org.cswteams.ms3.multitenancyapp.config.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SchemaSwitchingConnectionProvider implements MultiTenantConnectionProvider {

    private DataSource dataSource;

    public SchemaSwitchingConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {

    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = dataSource.getConnection();
        // Per le entità tenant, seleziona lo schema appropriato
        if (tenantIdentifier.equals("public")) {
            connection.createStatement().execute("SET search_path TO public");  // Per le tabelle di sistema
        } else {
            connection.createStatement().execute("SET search_path TO " + tenantIdentifier);  // Per i tenant specifici
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        // Niente di speciale in questa configurazione, dobbiamo solo rilasciare la connessione.
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class aClass) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }
}
