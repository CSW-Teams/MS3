package org.cswteams.ms3.multitenancyapp.config.multitenancy;

import org.cswteams.ms3.multitenancyapp.tenant.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import java.util.Objects;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getCurrentTenant();
        // Fallback
        return Objects.requireNonNullElse(tenant, "central_db");
    }


    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
