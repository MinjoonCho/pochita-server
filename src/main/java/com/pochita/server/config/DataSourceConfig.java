package com.pochita.server.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DataSourceConfig {

    private static final String H2_URL = "jdbc:h2:file:./data/pochita;MODE=PostgreSQL;AUTO_SERVER=TRUE";

    @Bean
    DataSource dataSource(Environment environment) {
        String explicitUrl = firstNonBlank(
                environment.getProperty("spring.datasource.url"),
                environment.getProperty("SPRING_DATASOURCE_URL"),
                environment.getProperty("JDBC_DATABASE_URL")
        );

        if (explicitUrl != null) {
            return buildDataSource(
                    explicitUrl,
                    firstNonBlank(environment.getProperty("spring.datasource.username"), environment.getProperty("SPRING_DATASOURCE_USERNAME"), environment.getProperty("PGUSER"), "sa"),
                    firstNonBlank(environment.getProperty("spring.datasource.password"), environment.getProperty("SPRING_DATASOURCE_PASSWORD"), environment.getProperty("PGPASSWORD"), ""),
                    explicitUrl.startsWith("jdbc:postgresql:") ? "org.postgresql.Driver" : "org.h2.Driver"
            );
        }

        String pgHost = environment.getProperty("PGHOST");
        if (isPresent(pgHost)) {
            String pgPort = firstNonBlank(environment.getProperty("PGPORT"), "5432");
            String pgDatabase = firstNonBlank(environment.getProperty("PGDATABASE"), "railway");
            String pgUser = firstNonBlank(environment.getProperty("PGUSER"), "postgres");
            String pgPassword = firstNonBlank(environment.getProperty("PGPASSWORD"), "");
            String jdbcUrl = "jdbc:postgresql://%s:%s/%s".formatted(pgHost, pgPort, pgDatabase);
            return buildDataSource(jdbcUrl, pgUser, pgPassword, "org.postgresql.Driver");
        }

        return buildDataSource(H2_URL, "sa", "", "org.h2.Driver");
    }

    private DataSource buildDataSource(String url, String username, String password, String driverClassName) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
