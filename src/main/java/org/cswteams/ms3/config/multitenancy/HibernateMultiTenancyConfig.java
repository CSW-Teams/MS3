package org.cswteams.ms3.config.multitenancy;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class HibernateMultiTenancyConfig {

    @Autowired
    private JpaProperties jpaProperties;

    @Bean
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
        return new CurrentTenantIdentifierResolverImpl();
    }

    @Bean
    public MultiTenantConnectionProvider schemaSwitchingConnectionProvider() {
        Map<String, DataSource> tenantDataSources = new HashMap<>();

        // Crea e aggiungi DataSource per ciascun tenant/schema
        tenantDataSources.put("public", DataSourceConfig.createTenantDataSource(
                "jdbc:postgresql://localhost:5432/ms3", "public_scheme_user", "password_public"
        ));
        tenantDataSources.put("a", DataSourceConfig.createTenantDataSource(
                "jdbc:postgresql://localhost:5432/ms3", "tenant_a_user", "password_a"
        ));
        tenantDataSources.put("b", DataSourceConfig.createTenantDataSource(
                "jdbc:postgresql://localhost:5432/ms3", "tenant_b_user", "password_b"
        ));

        return new SchemaSwitchingConnectionProviderPostgreSQL(tenantDataSources);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("org.cswteams.ms3.entity");

        JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);

        // Copiare tutte le proprietà JPA
        factoryBean.setJpaPropertyMap(jpaProperties.getProperties());

        factoryBean.getJpaPropertyMap().put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        factoryBean.getJpaPropertyMap().put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver());
        factoryBean.getJpaPropertyMap().put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, schemaSwitchingConnectionProvider());
        factoryBean.getJpaPropertyMap().put("hibernate.ddl-auto", "create");

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}