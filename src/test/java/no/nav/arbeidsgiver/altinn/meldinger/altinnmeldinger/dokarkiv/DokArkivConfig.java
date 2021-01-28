package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Configuration
public class DokArkivConfig {
    private final URI uri;

    @Autowired
    public DokArkivConfig(@Value("${dokarkiv.uri}") URI uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }

    @Bean
    RestTemplate restempate() {
        return new RestTemplate();
    }
}
