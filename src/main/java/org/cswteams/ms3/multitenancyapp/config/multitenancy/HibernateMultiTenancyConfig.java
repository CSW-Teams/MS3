package org.cswteams.ms3.multitenancyapp.config.multitenancy;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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

@Configuration
@EnableTransactionManagement
public class HibernateMultiTenancyConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
        return new CurrentTenantIdentifierResolverImpl();
    }

    @Bean
    public MultiTenantConnectionProvider schemaSwitchingConnectionProvider() {
        return new SchemaSwitchingConnectionProvider(dataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("org.cswteams.ms3.multitenancyapp.entity");

        JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);

        factoryBean.getJpaPropertyMap().put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        factoryBean.getJpaPropertyMap().put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver());
        factoryBean.getJpaPropertyMap().put("hibernate.ddl-auto", "create");
        factoryBean.getJpaPropertyMap().put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        factoryBean.getJpaPropertyMap().put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
        factoryBean.getJpaPropertyMap().put("hibernate.multi_tenant_connection_provider", schemaSwitchingConnectionProvider());

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
