package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.config;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.utils.SecureLog;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecureLogConfiguration {

    @Bean
    public SecureLog secureLog() {
        return new SecureLog(LoggerFactory.getLogger("secureLog"));
    }

}
