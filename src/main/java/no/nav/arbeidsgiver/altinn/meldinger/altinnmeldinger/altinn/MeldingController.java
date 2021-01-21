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
    private final MeldingLoggRepository meldingLoggRepository;

    public MeldingController(AltinnClient altinnClient, MeldingLoggRepository meldingLoggRepository) {
        this.altinnClient = altinnClient;
        this.meldingLoggRepository = meldingLoggRepository;
    }

    @PostMapping("/melding")
    public ResponseEntity<HttpStatus> sendAltinnMelding(
            @RequestBody AltinnMelding altinnMelding
    ) {
        MeldingLogg meldingLogg = MeldingLogg.from(altinnMelding);
        try {
            altinnClient.sendAltinnMelding(altinnMelding);
            meldingLogg.setStatus(MeldingStatus.OK);
        } catch (Exception e) {
            meldingLogg.setStatus(MeldingStatus.FEIL);
        }
        meldingLoggRepository.save(meldingLogg);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
