package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@EnableJwtTokenValidation(ignore = {"org.springframework", "org.springdoc"})
@SpringBootApplication
@Profile({"dev-gcp", "prod-gcp"})
public class AltinnMeldingerApplikasjon {

	public static void main(String[] args) {
		SpringApplication.run(AltinnMeldingerApplikasjon.class, args);
	}

}
