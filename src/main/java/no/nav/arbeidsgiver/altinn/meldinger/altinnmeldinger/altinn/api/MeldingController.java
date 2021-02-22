package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending.AltinnClient;
import no.nav.security.token.support.core.api.Protected;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Profile("!prod-gcp")
@Protected
@RestController
public class MeldingController {

    private final AltinnClient altinnClient;
    private final MeldingRepository meldingRepository;
    private final TokenValidationContextHolder contextHolder;

    @Value("${groupid}")
    private String groupId;

    public MeldingController(AltinnClient altinnClient, MeldingRepository meldingRepository, TokenValidationContextHolder contextHolder) {
        this.altinnClient = altinnClient;
        this.meldingRepository = meldingRepository;
        this.contextHolder = contextHolder;
    }

    @PostMapping("/melding")
    public ResponseEntity<HttpStatus> sendAltinnMelding(
            @RequestBody AltinnMeldingDTO altinnMeldingDTO
    ) {
        if (!harRettighet()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        meldingRepository.opprett(altinnMeldingDTO.tilMelding());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private boolean harRettighet() {
        Optional<Object> object = Optional.ofNullable(contextHolder.getTokenValidationContext().getClaims("aad").get("groups"));

        if (object.isEmpty()) {
            return false;
        }

        try {
            return object.map(groups -> ((List<String>) groups).contains(groupId)).orElse(false);
        } catch (Exception a) {
            throw new RuntimeException("Kunne ikke håndtere token");
        }
    }
}
