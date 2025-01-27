package org.cswteams.ms3.config.multitenancy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "tenants-datasources")
public class TenantProperties {

    private Map<String, DataSourceConfig> tenants;

    @Setter
    @Getter
    public static class DataSourceConfig {
        private String url;
        private String username;
        private String password;
        private String driver;
    }
}
