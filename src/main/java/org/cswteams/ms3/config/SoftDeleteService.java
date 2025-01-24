package org.cswteams.ms3.config;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Map;

/**
 * Service to manage the enabling and disabling of Hibernate filters for soft delete.
 * This service interacts with the Hibernate Session to configure filters dynamically.
 */
@Service
public class SoftDeleteService {
    @Autowired
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

        session.enableFilter("softDeleteFilter").setParameter("deleted", false);

//        org.hibernate.Filter filter = session.enableFilter(filterName);
//        if (parameters != null) {
//            parameters.forEach(filter::setParameter);
//        }
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