package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending.AltinnClient;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.featuretoggles.UnleashService;
import no.nav.security.token.support.core.api.Protected;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Protected
@RestController
public class MeldingController {

    private final MeldingRepository meldingRepository;
    private final TokenValidationContextHolder contextHolder;
    private final UnleashService unleashService;

    @Value("${tilgangskontroll.group}")
    private String group;

    public MeldingController(
            MeldingRepository meldingRepository, TokenValidationContextHolder contextHolder, UnleashService unleashService
    ) {
        this.meldingRepository = meldingRepository;
        this.contextHolder = contextHolder;
        this.unleashService = unleashService;
    }

    @PostMapping("/melding")
    public ResponseEntity<HttpStatus> sendAltinnMelding(
            @RequestBody AltinnMeldingDTO altinnMeldingDTO,
            @RequestHeader("idempotency-key") String idempotencyKey
    ) {
        if (!unleashService.erEnabled("altinn-meldinger-api.innsending")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!harRettighet()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        meldingRepository.opprett(altinnMeldingDTO.tilMelding(), idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private boolean harRettighet() {
        Optional<Object> object = Optional.ofNullable(contextHolder.getTokenValidationContext().getClaims("aad").get("groups"));

        if (object.isEmpty()) {
            return false;
        }

        try {
            return object.map(groups -> ((List<String>) groups).contains(group)).orElse(false);
        } catch (Exception a) {
            throw new RuntimeException("Kunne ikke håndtere token");
        }
    }
}
