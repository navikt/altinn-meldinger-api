package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.sikkerhet;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TilgangskontrollException extends RuntimeException {
    public TilgangskontrollException(String message) {super( message);}
}
