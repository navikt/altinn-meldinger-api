package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.AltinnMeldingDTO;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.PdfVedleggDTO;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.AltinnStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Melding;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"wiremock.port=8089"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EnableMockOAuth2Server
@ActiveProfiles("test")
public class ApiTest {

    @LocalServerPort
    private String port;

    @Autowired
    private MeldingRepository meldingRepository;

    @Autowired
    private MockOAuth2Server mockOAuth2Server;

    @Test
    public void api__skal_sende_melding_via_ws_og_returnere_created() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<PdfVedleggDTO> vedlegg = List.of(new PdfVedleggDTO(Base64.getEncoder().encodeToString("Dette er en test?".getBytes()), "Filnavn.txt", "Vedleggnavn"));
        AltinnMeldingDTO altinnMelding = new AltinnMeldingDTO(
                "999999999",
                "Dette er en melding som skal til Altinn",
                "Tittel!",
                "NAV_AGP2",
                "5562",
                "1",
                null,
                10,
                vedlegg);

        HttpResponse<String> response = newBuilder().build().send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/altinn-meldinger-api/melding"))
                        .header("Authorization", "Bearer " + token("aad", "subject", "audience"))
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(altinnMelding)))
                        .header("Content-Type", "application/json")
                        .build(),
                ofString()
        );

        assertThat(response.statusCode()).isEqualTo(201);
        List<Melding> meldingsLoggRader = meldingRepository.findAll();
        assertThat(meldingsLoggRader.size()).isEqualTo(1);
        Melding melding = meldingsLoggRader.get(0);
        assertThat(melding.getAltinnStatus()).isEqualTo(AltinnStatus.IKKE_SENDT);
        assertThat(melding.getAltinnReferanse()).isNull();
        assertThat(melding.getAltinnSendtTidspunkt()).isNull();

        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Melding> meldingsLoggRader2 = meldingRepository.findAll();
            assertThat(meldingsLoggRader2.size()).isEqualTo(1);
            assertThat(meldingsLoggRader2.get(0).getAltinnStatus()).isEqualTo(AltinnStatus.OK);
            Melding melding2 = meldingsLoggRader2.get(0);
            assertThat(melding2.getAltinnReferanse()).contains(melding2.getId());
            assertThat(melding2.getAltinnSendtTidspunkt()).isNotNull();
        });

    }

    @Test
    public void api__skal_validere_token() throws Exception {
        HttpResponse<String> response = newBuilder().build().send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/altinn-meldinger-api/melding"))
                        .POST(HttpRequest.BodyPublishers.ofString("{}"))
                        .header("Content-Type", "application/json")
                        .build(),
                ofString()
        );

        assertThat(response.statusCode()).isEqualTo(401);
    }

    private String token(String issuerId, String subject, String audience){
        return mockOAuth2Server.issueToken(
                issuerId,
                "theclientid",
                new DefaultOAuth2TokenCallback(
                        issuerId,
                        subject,
                        audience,
                        Collections.emptyMap(),
                        3600
                )
        ).serialize();
    }

}
