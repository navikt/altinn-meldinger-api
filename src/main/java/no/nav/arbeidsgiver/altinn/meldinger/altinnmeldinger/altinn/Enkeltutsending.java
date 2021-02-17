package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Melding;
import no.nav.security.token.support.core.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Unprotected
@RestController
public class Enkeltutsending {
    private final static Logger log = LoggerFactory.getLogger(Enkeltutsending.class);

    private final MeldingRepository meldingRepository;

    public Enkeltutsending(MeldingRepository meldingRepository) {
        this.meldingRepository = meldingRepository;
    }

    @PostMapping("/enkelutsending")
    private ResponseEntity<String> utførEnkelutsending() {
        Melding melding = new Melding(
                "id-enkelutsending",
                List.of("orgnr1", "orgnr2"),
                "melding",
                "tittel",
                "systemUsercode", // TODO NAV_AGP2?
                "9999", // TODO
                "1", // TODO
                null,
                2, // TODO
                List.of() // vedlegg

        );

        try {
            meldingRepository.opprett(melding);
            log.info("Enkeltutsending lagret");
            return ResponseEntity.ok("Enkeltutsending lagret");
        } catch (Error e) {
            log.info("En feil har skjedd under lagring av enkeltutsending");
            return ResponseEntity.status(500).body("En feil har skjedd");
        }
    }

}
