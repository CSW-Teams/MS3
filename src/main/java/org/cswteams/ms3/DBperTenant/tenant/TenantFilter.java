package org.cswteams.ms3.DBperTenant.tenant;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter implements ITenantFilter {

    private final TenantDataSourceProvider tenantDataSourceProvider;

    public TenantFilter(TenantDataSourceProvider tenantDataSourceProvider) {
        this.tenantDataSourceProvider = tenantDataSourceProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String tenantName = request.getHeader("X-Tenant-Name"); // Cambia con il tuo header o parametro
        if (tenantName != null) {
            String tenantId = tenantDataSourceProvider.getTenantIdByName(tenantName);
            if (tenantId != null) {
                TenantContext.setCurrentTenant(tenantId);
                System.out.println("Tenant impostato: " + tenantId);
            } else {
                System.err.println("Errore: tenantName non trovato: " + tenantName);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tenant non valido");
                return;
            }
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear(); // Pulisci il contesto alla fine
        }
    }
}
