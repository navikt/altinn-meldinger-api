package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending.AltinnClient;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.sikkerhet.Tilgangskontroll;
import no.nav.security.token.support.core.api.Protected;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Profile("!prod-gcp")
@Protected
@RestController
public class MeldingController {

    private final MeldingRepository meldingRepository;
    private final Tilgangskontroll tilgangskontroll;

    public MeldingController(MeldingRepository meldingRepository, Tilgangskontroll tilgangskontroll) {
        this.meldingRepository = meldingRepository;
        this.tilgangskontroll = tilgangskontroll;
    }

    @PostMapping("/melding")
    public ResponseEntity<HttpStatus> sendAltinnMelding(
            @RequestBody AltinnMeldingDTO altinnMeldingDTO
    ) {
        if (tilgangskontroll.harRettighet()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        meldingRepository.opprett(altinnMeldingDTO.tilMelding());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}