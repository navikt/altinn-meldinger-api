package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@Profile("local")
@SpringBootApplication
public class LokalApplikasjon {
    public static void main(String[] args) {
        SpringApplication.run(AltinnMeldingerApplikasjon.class, args);
    }
}
