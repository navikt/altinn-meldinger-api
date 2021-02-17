package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Melding;
import no.nav.security.token.support.core.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Unprotected
@RestController
public class Enkelutsending {
    private final static Logger log = LoggerFactory.getLogger(Enkelutsending.class);

    private final MeldingRepository meldingRepository;
    private final String id;
    private final List<String> orgnrs;
    private final String meldingstekst;

    public Enkelutsending(
            MeldingRepository meldingRepository,
            @Value("${enkelutsending.id}") String id,
            @Value("${enkelutsending.orgnrs}") String kommaseparerteOrgnrs,
            @Value("${enkelutsending.melding}") String meldingstekst
    ) {
        this.meldingRepository = meldingRepository;
        this.id = id;
        this.orgnrs = Arrays.asList(kommaseparerteOrgnrs.split(","));
        this.meldingstekst = meldingstekst;
    }

    @PostMapping("/enkelutsending")
    private ResponseEntity<String> utførEnkelutsending() {
        Melding melding = new Melding(
                id,
                orgnrs,
                meldingstekst,
                "tittel", // TODO
                "NAV_AGP2", // TODO
                "5562", // TODO
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
