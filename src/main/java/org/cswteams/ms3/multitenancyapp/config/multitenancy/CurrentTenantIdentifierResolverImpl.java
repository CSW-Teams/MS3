package org.cswteams.ms3.multitenancyapp.config.multitenancy;

import org.cswteams.ms3.multitenancyapp.tenant.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        // Recupera l'ID del tenant dal TenantContext
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            // Potresti voler lanciare una runtime exception o semplicemente restituire un tenant di default
            tenantId = "public";  // Assicurati di avere un "tenant di fallback"
        }
        return tenantId; // Ritorna il tenant corrente
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}