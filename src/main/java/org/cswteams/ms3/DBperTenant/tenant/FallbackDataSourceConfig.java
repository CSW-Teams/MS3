package org.cswteams.ms3.DBperTenant.tenant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class FallbackDataSourceConfig {

    @Bean
    @Primary
    public DataSource fallbackDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tenant_mapper"); // Imposta l'URL
        dataSource.setUsername("sprintfloyd");
        dataSource.setPassword("sprintfloyd");
        TenantContext.setCurrentTenant("tenant1");
        return dataSource;
    }
}

