package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AltinnWSConfig {
    @Bean
    public ICorrespondenceAgencyExternalBasic iCorrespondenceAgencyExternalBasic(
            AltinnConfig altinnConfig
    ) {
        return WsClient.createPort(altinnConfig.getUri(), ICorrespondenceAgencyExternalBasic.class);
    }
}
