package org.cswteams.ms3.config.multitenancy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {

    private Map<String, DataSource> tenantDataSources = new HashMap<>();
    public MultiTenantConnectionProviderImpl(Map<String, DataSource> tenantDataSources) {
        this.tenantDataSources = new ConcurrentHashMap<>(tenantDataSources);
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        // System.out.println("current tenant:" + tenantIdentifier);
        DataSource dataSource = tenantDataSources.get(tenantIdentifier);
        if (dataSource == null) {
            throw new HibernateException("No DataSource found for tenant: " + tenantIdentifier);
        }
        // System.out.println("Obtaining connection for tenant: " + tenantIdentifier);
        return dataSource.getConnection();
    }



    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return tenantDataSources.values().iterator().next().getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}
