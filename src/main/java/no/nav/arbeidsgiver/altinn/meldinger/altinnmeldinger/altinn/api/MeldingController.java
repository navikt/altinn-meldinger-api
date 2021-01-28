package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.AltinnClient;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.AltinnStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingLogg;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingLoggRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.DokArkivClient;
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
    private final DokArkivClient dokArkivClient;
    private final MeldingLoggRepository meldingLoggRepository;

    public MeldingController(AltinnClient altinnClient, DokArkivClient dokArkivClient, MeldingLoggRepository meldingLoggRepository) {
        this.altinnClient = altinnClient;
        this.dokArkivClient = dokArkivClient;
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
            dokArkivClient.journalførMelding(meldingLogg);
            } catch (Exception e) {
                meldingLogg.setStatus(AltinnStatus.FEIL);
            }
        meldingLoggRepository.save(meldingLogg);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

