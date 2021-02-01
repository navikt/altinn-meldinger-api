package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableMockOAuth2Server
@Profile("local")
public class LocalOauthConfig {
    private final String url;

    public LocalOauthConfig(
            @Value("${no.nav.security.jwt.issuer.saksbehandler.discoveryurl}") String url
    ) {
        this.url = url;
        System.out.println("@@@ " + url);
    }
}
