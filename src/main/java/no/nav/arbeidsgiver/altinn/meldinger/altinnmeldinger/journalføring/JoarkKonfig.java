package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.journalføring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Configuration
public class JoarkKonfig {
    private final URI uri;

    public JoarkKonfig(@Value("${joark.uri}") URI uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
