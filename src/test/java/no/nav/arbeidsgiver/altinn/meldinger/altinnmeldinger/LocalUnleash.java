package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.finn.unleash.FakeUnleash;
import no.finn.unleash.Unleash;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LocalUnleash {

    @Bean
    @Primary
    public Unleash getFakeUnleash() {
        FakeUnleash fakeUnleash = new FakeUnleash();
        fakeUnleash.enableAll();
        return fakeUnleash;
    }
}
