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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class HibernateMultiTenancyConfig {

    private static final Logger logger = LoggerFactory.getLogger(HibernateMultiTenancyConfig.class);

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

        // Recupero dinamico dei tenant dai nuovi parametri di application.properties
        String[] tenantNames = {
                environment.getProperty("spring.datasource.tenant.public.name"),
                environment.getProperty("spring.datasource.tenant.a.name"),
                environment.getProperty("spring.datasource.tenant.b.name")
        };

        String[] users = {
                environment.getProperty("spring.datasource.roles.public.username"),
                environment.getProperty("spring.datasource.roles.a.username"),
                environment.getProperty("spring.datasource.roles.b.username")
        };

        String[] passwords = {
                environment.getProperty("spring.datasource.roles.public.password"),
                environment.getProperty("spring.datasource.roles.a.password"),
                environment.getProperty("spring.datasource.roles.b.password")
        };

        String baseUrl = environment.getProperty("spring.datasource.tenant.public.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalArgumentException("L'URL di connessione al database principale non è definito in application.properties");
        }

        for (int i = 0; i < tenantNames.length; i++) {
            String tenant = tenantNames[i];
            if (tenant == null) continue;

            String user = users[i];
            String password = passwords[i];

            String tenantUrl = baseUrl.endsWith("/") ? baseUrl + tenant : baseUrl + "/" + tenant;
            System.out.println(tenantUrl);

            tenantDataSources.put(tenant, DataSourceConfig.createDataSource(tenantUrl, user, password));
        }

        logger.info("Initialized DataSources: {}", tenantDataSources.keySet());
        return new MultiTenantConnectionProviderImpl(tenantDataSources);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("org.cswteams.ms3.entity");

        JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);

        // Imposta le proprietà JPA da application.properties
        factoryBean.setJpaPropertyMap(jpaProperties.getProperties());

        factoryBean.getJpaPropertyMap().put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        factoryBean.getJpaPropertyMap().put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider());
        factoryBean.getJpaPropertyMap().put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver());
        factoryBean.getJpaPropertyMap().put("hibernate.ddl-auto", "none");

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
