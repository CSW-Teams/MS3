package org.cswteams.ms3.config.multitenancy;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
    private DataSource dataSource;

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private Environment environment;

    @Bean
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
        return new CurrentTenantIdentifierResolverImpl();
    }

    @Bean
    public MultiTenantConnectionProvider multiTenantConnectionProvider() {
        Map<String, DataSource> tenantDataSources = new HashMap<>();

        // Leggi i tenant e le credenziali da application.properties
        String[] tenants = environment.getProperty("spring.datasource.databases", "").split(",");
        String[] users = environment.getProperty("spring.datasource.users", "").split(",");
        String[] passwords = environment.getProperty("spring.datasource.passwords", "").split(",");

        if (tenants.length != users.length || tenants.length != passwords.length) {
            throw new IllegalArgumentException("Il numero di tenant, utenti e password non corrisponde nelle configurazioni.");
        }

        for (int i = 0; i < tenants.length; i++) {
            String tenant = tenants[i].trim();
            String user = users[i].trim();
            String password = passwords[i].trim();

            String url = String.format("jdbc:postgresql://localhost:5432/%s", tenant);
            tenantDataSources.put(tenant, DataSourceConfig.createDataSource(url, user, password));
        }

        System.out.println("Initialized DataSources:");
        tenantDataSources.forEach((key, value) -> System.out.println("Tenant: " + key));
        return new MultiTenantConnectionProviderImpl(tenantDataSources);
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

        factoryBean.getJpaPropertyMap().put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        factoryBean.getJpaPropertyMap().put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider());
        factoryBean.getJpaPropertyMap().put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver());
        factoryBean.getJpaPropertyMap().put("hibernate.ddl-auto", "update");

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
