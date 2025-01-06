package org.cswteams.ms3.DBperTenant.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class TenantDataSourceProvider {

    @Autowired
    @Lazy
    private DataSource centralDataSource;

    public Map<Object, Object> loadTenantDataSources() {
        Map<Object, Object> dataSources = new HashMap<>();
        try (Connection connection = centralDataSource.getConnection()) {
            String query = "SELECT tenant_id, database_url, username, password FROM tenant_mapping";
            ResultSet rs = connection.createStatement().executeQuery(query);
            while (rs.next()) {
                String tenantId = rs.getString("tenant_id");
                String url = rs.getString("database_url");
                String username = rs.getString("username");
                String password = rs.getString("password");

                DataSource tenantDataSource = DataSourceBuilder.create()
                        .url(url)
                        .username(username)
                        .password(password)
                        .driverClassName("org.postgresql.Driver")
                        .build();

                dataSources.put(tenantId, tenantDataSource);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel caricamento dei DataSource per i tenant", e);
        }
        return dataSources;
    }
}

