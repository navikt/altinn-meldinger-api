package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DokArkivConfig {
    private final String uri;
    private final String pdfGenUri;

    public DokArkivConfig(@Value("${dokarkiv.uri}") String uri, @Value("${pdfgen.uri}") String pdfGenUri) {
        this.uri = uri;
        this.pdfGenUri = pdfGenUri;
    }

    public String getUri() {
        return uri;
    }

    public String getPdfGenUri() {
        return pdfGenUri;
    }

    @Bean
    RestTemplate restempate() {
        return new RestTemplate();
    }
}