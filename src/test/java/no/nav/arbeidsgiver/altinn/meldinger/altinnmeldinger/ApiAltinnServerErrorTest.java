package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.AltinnMeldingDTO;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.JoarkTema;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.PdfVedleggDTO;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.AltinnStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending.AltinnConfig;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LokalApplikasjon.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@EnableMockOAuth2Server
@ActiveProfiles("test")
public class ApiAltinnServerErrorTest {

    private WireMockServer wireMockServer;

    @Autowired
    private AltinnConfig altinnConfig;

    @LocalServerPort
    private String webAppPort;

    @Autowired
    private MeldingRepository meldingRepository;

    @Autowired
    private MockOAuth2Server mockOAuth2Server;

    @Value("${wiremock.port}") Integer wiremockPort;

    @BeforeEach
    public void setup() throws Exception {
        String altinnPath = new URL(altinnConfig.getUri()).getPath();
        this.wireMockServer = new WireMockServer(
                options()
                        .extensions(new ResponseTemplateTransformer(false))
                        .port(wiremockPort));
        this.wireMockServer.stubFor(post(altinnPath).willReturn(
                aResponse()
                        .withStatus(500)
                        .withUniformRandomDelay(50, 500)));
        this.wireMockServer.start();
    }

    @AfterEach
    public void afterEach() {
        this.wireMockServer.stop();
    }

    @Test
    public void api__skal_sende_melding_til_altinn_og_håndtere_500_respons() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<PdfVedleggDTO> vedlegg = List.of(
                new PdfVedleggDTO(Base64.getEncoder().encodeToString("Dette er en test?".getBytes()), "Filnavn.txt", "Vedleggnavn"),
                new PdfVedleggDTO(Base64.getEncoder().encodeToString("Dette er en ny fil".getBytes()), "Filnavn2.txt", "Vedleggnavn2")
        );
        AltinnMeldingDTO altinnMelding = new AltinnMeldingDTO(
                List.of("999999999"),
                "Dette er en melding som skal til Altinn",
                "Tittel!",
                "NAV_AGP2",
                "5562",
                "1",
                null,
                10,
                JoarkTema.PER,
                vedlegg);

        HttpResponse<String> response = newBuilder().build().send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + webAppPort + "/altinn-meldinger-api/melding"))
                        .header("Content-Type", "application/json")
                        .header("idempotency-key", Ulider.nextULID())
                        .header("Authorization", "Bearer " + TestUtils.token(mockOAuth2Server, "aad", "subject", "altinn-meldinger-api", "rettighet-for-å-bruke-apiet-lokalt"))
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(altinnMelding)))
                        .build(),
                ofString()
        );
        assertThat(response.statusCode()).isEqualTo(201);

        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(meldingRepository.hentMedAltinnStatus(AltinnStatus.IKKE_SENDT)).isEmpty();
            assertThat(meldingRepository.hentMedAltinnStatus(AltinnStatus.FEIL).size()).isEqualTo(1);
        });
    }
}
