package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Testdata.lesFilSomString;

@Profile("local")
@Component
public class MockServer {
    public static final boolean AKTIVER_VERBOSE_LOGGING_I_KONSOLEN = false;
    private final WireMockServer server;

    public MockServer(
            @Value("${wiremock.port}") Integer port
    ) {
        this.server = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(port)
                        .notifier(new ConsoleNotifier(AKTIVER_VERBOSE_LOGGING_I_KONSOLEN))
        );

        server.stubFor(WireMock.post("/ekstern/altinn/BehandleAltinnMelding/v1").willReturn(
                WireMock.ok().withBody(lesFilSomString("altinn_response_200.xml")))
        );
        server.start();
    }
}
