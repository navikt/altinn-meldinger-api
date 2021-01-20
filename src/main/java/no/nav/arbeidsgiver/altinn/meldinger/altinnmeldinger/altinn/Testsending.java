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
        altinnClient.sendAltinnMelding(new AltinnMelding(
                bedriftsnr,
                "Body på testmelding",
                "Dette er en testmelding"
        ));
        LOGGER.info("Altinn-melding sendt til " + bedriftsnr);
    }
}
