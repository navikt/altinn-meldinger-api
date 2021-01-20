package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Testsending {
    private final Logger LOGGER = LoggerFactory.getLogger(Testsending.class);

    public Testsending(
            @Value("${testsending.bedriftsnr}") String bedriftsnr,
            AltinnClient altinnClient
    ) {
        LOGGER.info("Starter sending av Altinn-melding til " + bedriftsnr);
        try {
            altinnClient.sendAltinnMelding(new AltinnMelding(
                    bedriftsnr,
                    "Body på testmelding",
                    "Dette er en testmelding"
            ));
        } catch (Exception e) {
            LOGGER.error("error", e);
        }
        LOGGER.info("Altinn-melding sendt til " + bedriftsnr);
    }
}
