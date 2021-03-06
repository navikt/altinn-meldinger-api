package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending.AltinnConfig;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.DokArkivConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URL;

import static no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Testdata.lesFilSomString;

@Component
@Profile({"local"})
public class MockServer {
    public static final boolean AKTIVER_VERBOSE_LOGGING_I_KONSOLLEN = true;

    private final WireMockServer server;

    private final String altinnResponse200 = lesFilSomString("altinn_response_200.xml");
    private final String altinnResponse500 = lesFilSomString("altinn_response_500.xml");
    private final String dokarkivResponse200 = lesFilSomString("dokarkiv_response_200.json");

    public MockServer(
            AltinnConfig altinnConfig, DokArkivConfig dokArkivConfig,
            @Value("${wiremock.port}") Integer port
    ) throws Exception {
        this.server = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(port)
                        .notifier(new ConsoleNotifier(AKTIVER_VERBOSE_LOGGING_I_KONSOLLEN))
        );

        String altinnPath = new URL(altinnConfig.getUri()).getPath();
        String dokarkivPath = new URL(dokArkivConfig.getUri()).getPath() + "?forsoekFerdigstill=true";
        String pdfGenPath = new URL(dokArkivConfig.getPdfGenUri()).getPath();

        if(altinnPath.endsWith("failure")) {
            server.stubFor(WireMock.post(altinnPath).willReturn(
                    WireMock.aResponse()
                            .withStatus(500)
                            .withUniformRandomDelay(50, 500))
            );
        } else if (altinnPath.endsWith("errorCode")) {
            server.stubFor(WireMock.post(altinnPath).willReturn(
                    WireMock.aResponse()
                            .withStatus(500)
                            .withHeader("Content-Type", "application/soap+xml")
                            .withBody(altinnResponse500))
            );
        } else {
            server.stubFor(WireMock.post(altinnPath).willReturn(
                    WireMock.ok()
                            .withUniformRandomDelay(50, 500)
                            .withBody(altinnResponse200))
            );
        }


        server.stubFor(WireMock.post(pdfGenPath).willReturn(WireMock.okJson("{\"pdf\" : \"" + "ok".getBytes() + "\"}")));
        server.stubFor(WireMock.post(dokarkivPath).willReturn(WireMock.okJson("{\"journalpostId\" : \"493329380\", \"journalstatus\" : \"ENDELIG\", \"melding\" : \"Gikk bra\"}")));
        server.start();
    }
}