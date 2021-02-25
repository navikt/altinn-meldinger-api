package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.utils;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;

@AllArgsConstructor
public class SecureLog {
    private final Logger logger;

    public void log(String txt) {
        if (logger != null) { // logger is only present when running on nais
            logger.info(txt);
        }
    }

}
