package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.juli.logging.LogFactory;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Profile({"dev-gcp", "prod-gcp"})
@Configuration
public class DatabaseConfig {
    private final static Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    private final String url;
    private final String username;
    private final String password;

    public DatabaseConfig(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.flyway.locations}") String locations
    ) {
        this.url = url;
        this.username = username;
        this.password = password;
        log.info("spring.flyway.locations " + locations);
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(2);
        config.setMinimumIdle(1);
        config.setInitializationFailTimeout(60000);

        return new HikariDataSource(config);
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> Flyway.configure()
                .dataSource(dataSource())
                .load()
                .migrate();
    }
}
