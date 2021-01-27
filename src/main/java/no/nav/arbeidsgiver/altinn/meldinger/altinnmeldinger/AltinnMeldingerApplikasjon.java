package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile({"dev-gcp", "prod-gcp"})
@EnableOAuth2Client(cacheEnabled = true)
public class AltinnMeldingerApplikasjon {

	public static void main(String[] args) {
		SpringApplication.run(AltinnMeldingerApplikasjon.class, args);
	}

}
