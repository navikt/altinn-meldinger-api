package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Testdata.lesFilSomString;
import static no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.AltinnWSConfig.SEND_ALTINN_MELDING_API_PATH;

@Profile("local")
@Component
public class MockServer {
    public static final boolean AKTIVER_VERBOSE_LOGGING_I_KONSOLLEN = false;
    private final WireMockServer server;

    private final String altinnResponse200 = lesFilSomString("altinn_response_200.xml");
    private final String altinnResponse500 = lesFilSomString("altinn_response_500.xml");

    public MockServer(
            @Value("${wiremock.port}") Integer port
    ) {
        this.server = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(port)
                        .notifier(new ConsoleNotifier(AKTIVER_VERBOSE_LOGGING_I_KONSOLLEN))
        );

        server.stubFor(WireMock.post("/altinn" + SEND_ALTINN_MELDING_API_PATH).willReturn(
                WireMock.ok().withBody(altinnResponse200))
        );
        server.start();
    }
}
