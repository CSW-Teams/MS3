package org.cswteams.ms3.config;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@Configuration
@EnableTransactionManagement
public class HibernateListenerConfig {

    @Autowired
    private LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        SessionFactory sessionFactory = (SessionFactory) entityManagerFactoryBean.getNativeEntityManagerFactory();

        EventListenerRegistry registry = ((org.hibernate.internal.SessionFactoryImpl) sessionFactory)
                .getServiceRegistry()
                .getService(EventListenerRegistry.class);

        SoftDeleteSessionEventListener listener = applicationContext.getBean(SoftDeleteSessionEventListener.class);

        // Register listener
        registry.getEventListenerGroup(org.hibernate.event.spi.EventType.POST_LOAD).appendListener(listener);
        registry.getEventListenerGroup(org.hibernate.event.spi.EventType.PRE_UPDATE).appendListener(listener);
    }
}