package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending.AltinnClient;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.AltinnStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.MeldingLogg;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingLoggRepository;
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
    private final MeldingLoggRepository meldingLoggRepository;

    public MeldingController(AltinnClient altinnClient, MeldingLoggRepository meldingLoggRepository) {
        this.altinnClient = altinnClient;
        this.meldingLoggRepository = meldingLoggRepository;
    }

    @PostMapping("/melding")
    public ResponseEntity<HttpStatus> sendAltinnMelding(
            @RequestBody AltinnMeldingDTO altinnMeldingDTO
    ) {
        MeldingLogg meldingLogg = altinnMeldingDTO.toMeldingLogg();
        try {
                altinnClient.sendAltinnMelding(meldingLogg);
                meldingLogg.setStatus(AltinnStatus.OK);
            } catch (Exception e) {
                meldingLogg.setStatus(AltinnStatus.FEIL);
            }
        meldingLoggRepository.save(meldingLogg);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
