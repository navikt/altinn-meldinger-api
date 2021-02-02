package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
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
    private final MeldingRepository meldingRepository;

    public MeldingController(AltinnClient altinnClient, MeldingRepository meldingRepository) {
        this.altinnClient = altinnClient;
        this.meldingRepository = meldingRepository;
    }

    @PostMapping("/melding")
    public ResponseEntity<HttpStatus> sendAltinnMelding(
            @RequestBody AltinnMeldingDTO altinnMeldingDTO
    ) {
        meldingRepository.save(altinnMeldingDTO.toMeldingLogg());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
