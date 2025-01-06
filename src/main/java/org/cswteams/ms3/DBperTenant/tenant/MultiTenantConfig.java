package org.cswteams.ms3.DBperTenant.tenant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.context.annotation.DependsOn;


import javax.sql.DataSource;

@Configuration
public class MultiTenantConfig {

    @Bean
    public DataSource multiTenantDataSource(TenantDataSourceProvider tenantDataSourceProvider) {
        MultiTenantDataSource dataSource = new MultiTenantDataSource();
        dataSource.setTargetDataSources(tenantDataSourceProvider.loadTenantDataSources());
        dataSource.afterPropertiesSet();
        return new LazyConnectionDataSourceProxy(dataSource); // Proxy per il DataSource
    }
}


