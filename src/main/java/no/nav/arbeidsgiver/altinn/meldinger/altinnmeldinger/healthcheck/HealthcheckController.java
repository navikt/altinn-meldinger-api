package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.healthcheck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthcheckController {
    @GetMapping("/internal/healthcheck")
    public String healthcheck() {
        return "OK";
    }
}
