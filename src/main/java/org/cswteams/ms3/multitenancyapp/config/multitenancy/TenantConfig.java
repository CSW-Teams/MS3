package org.cswteams.ms3.multitenancyapp.config.multitenancy;

import java.util.List;

public class TenantConfig {

    private List<String> tenants;

    // Getter e Setter
    public List<String> getTenants() {
        return tenants;
    }

    public void setTenants(List<String> tenants) {
        this.tenants = tenants;
    }
}
