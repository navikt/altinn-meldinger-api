package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingLoggRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending.AltinnClient;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Protected
@Profile("!prod-gcp")
@RestController
public class MeldingController {

    private final AltinnClient altinnClient;
    private final MeldingLoggRepository meldingLoggRepository;

    public MeldingController(AltinnClient altinnClient, MeldingLoggRepository meldingLoggRepository) {
        this.altinnClient = altinnClient;
        this.meldingLoggRepository = meldingLoggRepository;
    }

    @PostMapping("/melding")
    public ResponseEntity<HttpStatus> sendAltinnMelding(
            @RequestBody AltinnMeldingDTO altinnMeldingDTO
    ) {
        meldingLoggRepository.save(altinnMeldingDTO.toMeldingLogg());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
