package org.cswteams.ms3.config.multitenancy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class TenantMapper {

    private static final Map<String, String> tenantMapping = new HashMap<>();

    @Value("${spring.datasource.tenant.public.name}")
    private static String publicTenant;

    @Value("${spring.datasource.tenant.a.name}")
    private String tenantA;

    @Value("${spring.datasource.tenant.b.name}")
    private String tenantB;

    @PostConstruct
    public void init() {
        tenantMapping.put("A", tenantA);
        tenantMapping.put("B", tenantB);
        tenantMapping.put("PUBLIC", publicTenant);
    }

    public static String getTenantMap(String key) {
        return tenantMapping.getOrDefault(key, publicTenant); // Fallback a PUBLIC se non trovato
    }
}

