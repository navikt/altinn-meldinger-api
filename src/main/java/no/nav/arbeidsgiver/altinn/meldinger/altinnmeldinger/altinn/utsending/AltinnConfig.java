package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AltinnConfig {
    private final String uri;
    private final String brukernavn;
    private final String passord;

    public AltinnConfig(
            @Value("${altinn.uri}") String uri,
            @Value("${altinn.brukernavn}") String brukernavn,
            @Value("${altinn.passord}") String passord
    ) {
        this.uri = uri;
        this.brukernavn = brukernavn;
        this.passord = passord;
    }

    public String getUri() {
        return uri;
    }

    public String getBrukernavn() {
        return brukernavn;
    }

    public String getPassord() {
        return passord;
    }
}
