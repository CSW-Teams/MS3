package org.cswteams.ms3.audit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit")
public class AuditProperties {
    /**
     * When enabled, audit events are also persisted to the database.
     */
    private boolean persistenceEnabled = false;

    public boolean isPersistenceEnabled() {
        return persistenceEnabled;
    }

    public void setPersistenceEnabled(boolean persistenceEnabled) {
        this.persistenceEnabled = persistenceEnabled;
    }
}
