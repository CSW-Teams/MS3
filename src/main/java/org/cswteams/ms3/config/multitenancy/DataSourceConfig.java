package org.cswteams.ms3.config.multitenancy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Map;

public class DataSourceConfig {
    static DataSource createDataSource(String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        //config.addDataSourceProperty("connectionInitSql", "SET search_path TO public");

        return new HikariDataSource(config);
    }

}
