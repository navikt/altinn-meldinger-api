package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@EnableJwtTokenValidation(ignore = {"org.springframework", "org.springdoc"})
@Profile({"test", "local"})
@SpringBootApplication
public class LokalApplikasjon {
    public static void main(String[] args) {
        SpringApplication.run(LokalApplikasjon.class, args);
    }
}
