package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.security.token.support.core.api.Protected;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Protected
@RestController
public class ProtectedController {
    @GetMapping("/protected")
    public String protectedEndepunkt() {
        return "Du har tilgang!";
    }
}
