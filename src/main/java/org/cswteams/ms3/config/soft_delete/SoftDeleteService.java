package org.cswteams.ms3.config.soft_delete;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

/**
 * Service to manage the enabling and disabling of Hibernate filters for soft delete.
 * This service interacts with the Hibernate Session to configure filters dynamically.
 */
@Service
public class SoftDeleteService {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Enables the specified Hibernate filter and sets its parameters dynamically.
     *
     * @param filterName The name of the filter to enable.
     * @param parameters A map containing parameter names as keys and their corresponding values.
     */
    @Transactional
    public void enableSoftDeleteFilter(String filterName, Map<String, Object> parameters) {
        Session session = entityManager.unwrap(Session.class);

        Logger logger = LoggerFactory.getLogger(SoftDeleteService.class);
        logger.info("Session ID: {}", session.hashCode());

        if (session.getEnabledFilter(filterName) != null)
            logger.info("Session PRE - ID: {}, enabledFilter: {}", session.hashCode(), session.getEnabledFilter(filterName).toString());

        session.enableFilter("softDeleteFilter").setParameter("isDeleted", false);

        if (session.getEnabledFilter(filterName) != null)
            logger.info("Session POST - ID: {}, enabledFilter: {}", session.hashCode(), session.getEnabledFilter(filterName).toString());
    }

    /**
     * Disables the specified soft delete filter.
     *
     * @param filterName The name of the filter to disable.
     */
    @Transactional
    public void disableSoftDeleteFilter(String filterName) {
        Session session = entityManager.unwrap(Session.class);
        session.disableFilter(filterName);
    }
}