package org.cswteams.ms3.DBperTenant.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Component
public class TenantDataSourceProvider {

    @Autowired
    @Lazy
    private DataSource centralDataSource;

    public Map<String, DataSource> loadTenantDataSources() {
        Map<String, DataSource> dataSources = new HashMap<>();
        try (Connection connection = centralDataSource.getConnection()) {
            String query = "SELECT tenant_id, tenant_url, tenant_username, tenant_password FROM tenant_mapping";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String tenantId = rs.getString("tenant_id");
                String url = rs.getString("tenant_url");
                String username = rs.getString("tenant_username");
                String password = rs.getString("tenant_password");

                DataSource tenantDataSource = DataSourceBuilder.create()
                        .url(url)
                        .username(username)
                        .password(password)
                        .driverClassName("org.postgresql.Driver")
                        .build();

                // Debug del contenuto del DataSource
                System.out.println("Tenant ID: " + tenantId);
                System.out.println("URL: " + url);
                System.out.println("Username: " + username);

                dataSources.put(tenantId, tenantDataSource);
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il caricamento dei DataSource per i tenant", e);
        }
        return dataSources;
    }

    public String getTenantIdByName(String tenantName) {
        String tenantId = null;
        try (Connection connection = centralDataSource.getConnection()) {
            String query = "SELECT tenant_id FROM tenant_mapping WHERE tenant_name = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, tenantName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tenantId = rs.getString("tenant_id");
            } else {
                System.err.println("Tenant non trovato per il nome: " + tenantName);
            }
        } catch (Exception e) {
            System.err.println("Errore durante il recupero del tenant_id: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Tenant Name: " + tenantName + " -> Tenant ID: " + tenantId);
        return tenantId;
    }


}

