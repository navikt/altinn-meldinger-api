package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AltinnWSConfig {
    public final static String SEND_ALTINN_MELDING_API_PATH = "/ServiceEngineExternal/CorrespondenceAgencyExternalBasic.svc";

    @Bean
    public ICorrespondenceAgencyExternalBasic iCorrespondenceAgencyExternalBasic(
            AltinnConfig altinnConfig
    ) {
        return WsClient.createPort(
                altinnConfig.getUri() + SEND_ALTINN_MELDING_API_PATH,
                ICorrespondenceAgencyExternalBasic.class
        );
    }
}
