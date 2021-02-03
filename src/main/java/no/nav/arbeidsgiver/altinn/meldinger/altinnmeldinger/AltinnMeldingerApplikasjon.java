package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableJwtTokenValidation(ignore = {"org.springframework", "org.springdoc"})
@SpringBootApplication
public class AltinnMeldingerApplikasjon {

	public static void main(String[] args) {
		SpringApplication.run(AltinnMeldingerApplikasjon.class, args);
	}

}
