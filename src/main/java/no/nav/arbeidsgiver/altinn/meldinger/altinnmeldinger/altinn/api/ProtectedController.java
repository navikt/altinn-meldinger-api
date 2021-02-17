package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.security.token.support.core.api.Protected;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Protected
@RestController
public class ProtectedController {

    @Autowired
    private TokenValidationContextHolder contextHolder;

    @Value("${groupid}")
    private String groupId;

    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndepunkt() {
        if (!harRettighet()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok("Du har tilgang!");
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
