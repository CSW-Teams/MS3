package org.cswteams.ms3.config.multitenancy;

import org.cswteams.ms3.tenant.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Value("${spring.datasource.tenant.public.name}")
    private String public_db;

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getCurrentTenant();
        // Fallback to default tenant if no tenant is set in context
        return Objects.requireNonNullElse(tenant, public_db);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
