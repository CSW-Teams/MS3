package org.cswteams.ms3.config.multitenancy;

import org.cswteams.ms3.tenant.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    private static final String DEFAULT_TENANT = "public";

    @Override
    public String resolveCurrentTenantIdentifier() {
        // Recupera il tenant dal TenantContext
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            // Potresti voler lanciare una runtime exception o semplicemente restituire un tenant di default
            tenantId = DEFAULT_TENANT;  // Assicurati di avere un "tenant di fallback"
        }
        return tenantId; // Ritorna il tenant corrente
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}