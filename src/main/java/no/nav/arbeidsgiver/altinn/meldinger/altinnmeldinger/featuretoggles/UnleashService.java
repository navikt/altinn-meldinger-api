package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.featuretoggles;

import no.finn.unleash.Unleash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnleashService {
    private final Unleash unleash;

    @Autowired
    public UnleashService(Unleash unleash) {
        this.unleash = unleash;
    }

    public boolean erEnabled(String feature) {
        return unleash.isEnabled(feature);
    }
}
