package org.cswteams.ms3.config.multitenancy;

import java.util.Map;

public class TenantMapper {

    private static final Map<String, String> tenantMapping = Map.of(
            "A", "ms3_a",
            "B", "ms3_b",
            "ms3_public", "ms3_public"
    );

    public static String getTenantMap(String key) {
        return tenantMapping.get(key);
    }
}
