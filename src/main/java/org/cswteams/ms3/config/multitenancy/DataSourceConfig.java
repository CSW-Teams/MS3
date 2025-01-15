package org.cswteams.ms3.config.multitenancy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DataSourceConfig {

    public static DataSource createTenantDataSource(String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");

        // Configurazioni aggiuntive (se necessarie)
        config.setAutoCommit(true);

        return new HikariDataSource(config);
    }
}
