package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.sikkerhet;

import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.TimeLimitExceededException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class Tilgangskontroll {
    private final TokenValidationContextHolder contextHolder;
    private final Logger secureLog = LoggerFactory.getLogger("secureLog");
    private final String group;

    public Tilgangskontroll(TokenValidationContextHolder contextHolder, @Value("${tilgangskontroll.group}") String group) {
        this.contextHolder = contextHolder;
        this.group = group;
    }

    public void sjekkRettighetOgLoggSikkerhetshendelse(String url, String metode, Map<String, String> ekstraParametere) {
        boolean harRettighet = harRettighet();
        secureLog.info("Bruker: {}, har tilgang: {}, gruppe : {}, url : {}, ekstra parametere : {}",
                contextHolder.getTokenValidationContext().getClaims("aad").get("NAVident"),
                harRettighet,
                group,
                ekstraParametere.toString());
        if (!harRettighet) {
            throw new TilgangskontrollException("ingen tilgangang");
        }
    }

    public boolean harRettighet() {
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
