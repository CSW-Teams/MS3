package org.cswteams.ms3.multitenancyapp.config.multitenancy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {

    private final Map<String, DataSource> tenantDataSources = new HashMap<>();

    public MultiTenantConnectionProviderImpl() {
        // Configura i DataSource per ciascun tenant
        tenantDataSources.put("central_db", createDataSource("jdbc:postgresql://localhost:5432/central_db"));
        tenantDataSources.put("a", createDataSource("jdbc:postgresql://localhost:5432/a"));
        tenantDataSources.put("b", createDataSource("jdbc:postgresql://localhost:5432/b"));

        System.out.println("Initialized DataSources:");
        tenantDataSources.forEach((key, value) -> System.out.println("Tenant: " + key));
    }

    private DataSource createDataSource(String url) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername("sprintfloyd"); // Cambia con il tuo username
        config.setPassword("sprintfloyd"); // Cambia con la tua password
        return new HikariDataSource(config);
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        DataSource dataSource = tenantDataSources.get(tenantIdentifier);
        if (dataSource == null) {
            throw new HibernateException("No DataSource found for tenant: " + tenantIdentifier);
        }
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
