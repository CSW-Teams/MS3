package org.cswteams.ms3.config.multitenancy;

import org.cswteams.ms3.tenant.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import java.util.Objects;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    private static final String DEFAULT_TENANT = "ms3_public";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getCurrentTenant();
        // Fallback
        return Objects.requireNonNullElse(tenant, DEFAULT_TENANT);
    }


    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}