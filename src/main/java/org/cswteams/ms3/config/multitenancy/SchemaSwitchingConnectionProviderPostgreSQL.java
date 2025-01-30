package org.cswteams.ms3.config.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchemaSwitchingConnectionProviderPostgreSQL implements MultiTenantConnectionProvider {

    private final Map<String, DataSource> tenantDataSources;

    public SchemaSwitchingConnectionProviderPostgreSQL(Map<String, DataSource> tenantDataSources) {
        this.tenantDataSources = new ConcurrentHashMap<>(tenantDataSources);
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        // Restituisce una connessione generica (es. per scopi amministrativi)
        if (tenantDataSources.isEmpty()) {
            throw new SQLException("No data sources configured for tenants!");
        }
        return tenantDataSources.values().iterator().next().getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        DataSource dataSource = tenantDataSources.get(tenantIdentifier);

        if (dataSource == null) {
            throw new SQLException("Tenant not found: " + tenantIdentifier);
        }

        Connection connection = dataSource.getConnection();
        connection.createStatement().execute("SET search_path TO " + tenantIdentifier);
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.createStatement().execute("RESET search_path");
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