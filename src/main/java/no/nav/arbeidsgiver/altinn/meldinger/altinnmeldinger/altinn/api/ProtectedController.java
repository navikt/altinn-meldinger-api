package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.sikkerhet.Tilgangskontroll;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Protected
@RestController
public class ProtectedController {

    private final Tilgangskontroll tilgangskontroll;

    public ProtectedController(Tilgangskontroll tilgangskontroll) {
        this.tilgangskontroll = tilgangskontroll;
    }

    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndepunkt(HttpServletRequest request) {
        tilgangskontroll.sjekkRettighetOgLoggSikkerhetshendelse(
                request.getRequestURL().toString(),
                request.getMethod(),
                null
        );
        return ResponseEntity.ok("Du har tilgang!");
    }
}
