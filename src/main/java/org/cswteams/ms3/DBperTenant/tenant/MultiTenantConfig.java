package org.cswteams.ms3.DBperTenant.tenant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MultiTenantConfig {

    private final DataSource fallbackDataSource;

    public MultiTenantConfig(DataSource fallbackDataSource) {
        this.fallbackDataSource = fallbackDataSource;
    }

    @Bean
    public DataSource multiTenantDataSource(TenantDataSourceProvider tenantDataSourceProvider) {
        MultiTenantDataSource dataSource = new MultiTenantDataSource();
        Map<String, DataSource> tenantDataSources = tenantDataSourceProvider.loadTenantDataSources();

        dataSource.setTargetDataSources(new HashMap<>(tenantDataSources));
        dataSource.setDefaultTargetDataSource(fallbackDataSource); // Usa il bean esistente
        dataSource.afterPropertiesSet();

        return new LazyConnectionDataSourceProxy(dataSource);
    }
}
