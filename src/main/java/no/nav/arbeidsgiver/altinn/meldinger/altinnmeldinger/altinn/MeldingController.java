package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Profile({"local", "dev-gcp"})
@RestController
public class MeldingController {

    private final AltinnClient altinnClient;

    public MeldingController(AltinnClient altinnClient) {
        this.altinnClient = altinnClient;
    }

    @PostMapping("/melding")
    public ResponseEntity<HttpStatus> sendAltinnMelding(
            @RequestBody AltinnMelding altinnMelding
    ) {
        altinnClient.sendAltinnMelding(altinnMelding);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
