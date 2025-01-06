package org.cswteams.ms3.DBperTenant.tenant;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader("X-Tenant-ID"); // Recupera il tenant ID dall'header
        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false; // Rifiuta la richiesta se il tenant ID è mancante
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear(); // Pulisce il contesto dopo la richiesta
    }
}
