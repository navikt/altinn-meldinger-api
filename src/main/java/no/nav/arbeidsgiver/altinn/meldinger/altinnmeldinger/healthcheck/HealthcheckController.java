package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.healthcheck;

import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Unprotected
@RestController
public class HealthcheckController {
    @GetMapping("/internal/healthcheck")
    public String healthcheck() {
        return "OK";
    }
}
