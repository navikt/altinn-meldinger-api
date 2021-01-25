package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {
    private final String jdbcUrl;
    private final static Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    public DatabaseConfig(@Value("${spring.datasource.url}") String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        log.info("JDBC-url: " + jdbcUrl);
    }
}
