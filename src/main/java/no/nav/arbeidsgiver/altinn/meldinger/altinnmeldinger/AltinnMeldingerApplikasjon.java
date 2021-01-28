package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile({"dev-gcp", "prod-gcp"})
public class AltinnMeldingerApplikasjon {

	public static void main(String[] args) {
		SpringApplication.run(AltinnMeldingerApplikasjon.class, args);
	}

}
