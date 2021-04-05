package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.AltinnMeldingDTO;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.JoarkTema;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.PdfVedleggDTO;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.AltinnStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.JoarkStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending.AltinnConfig;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.DokArkivConfig;
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

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Testdata.lesFilSomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LokalApplikasjon.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@EnableMockOAuth2Server
@ActiveProfiles("test")
public class ApiTest {

    private WireMockServer wireMockServer;

    private final String altinnResponse200 = lesFilSomString("altinn_response_200.xml");

    @Autowired
    private AltinnConfig altinnConfig;

    @Autowired
    private DokArkivConfig dokArkivConfig;

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
        String dokarkivPath = new URL(dokArkivConfig.getUri()).getPath() + "?forsoekFerdigstill=true";
        String pdfGenPath = new URL(dokArkivConfig.getPdfGenUri()).getPath();

        this.wireMockServer = new WireMockServer(
                options()
                        .extensions(new ResponseTemplateTransformer(false))
                        .port(wiremockPort));
        this.wireMockServer.stubFor(post(altinnPath).willReturn(
                ok()
                        .withUniformRandomDelay(50, 500)
                        .withBody(altinnResponse200)));
        this.wireMockServer.stubFor(post(pdfGenPath).willReturn(okJson("{\"pdf\" : \"" + "ok".getBytes() + "\"}")));
        this.wireMockServer.stubFor(post(dokarkivPath).willReturn(okJson("{\"journalpostId\" : \"493329380\", \"journalstatus\" : \"ENDELIG\", \"melding\" : \"Gikk bra\"}")));

        this.wireMockServer.start();
    }

    @AfterEach
    public void afterEach() {
        this.wireMockServer.stop();
    }

    @Test
    public void api__skal_sende_melding_til_altinn_og_sende_til_joark_og_returnere_created() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<PdfVedleggDTO> vedlegg = List.of(
                new PdfVedleggDTO(Base64.getEncoder().encodeToString("Dette er en test?".getBytes()), "Filnavn.txt", "Vedleggnavn"),
                new PdfVedleggDTO(Base64.getEncoder().encodeToString("Dette er en ny fil".getBytes()), "Filnavn2.txt", "Vedleggnavn2")
        );
        AltinnMeldingDTO altinnMelding = new AltinnMeldingDTO(
                List.of("999999999", "888888888"),
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

        assertThat(meldingRepository.hentMedAltinnStatus(AltinnStatus.IKKE_SENDT)
                .stream()
                .map(p -> p.getOrgnr())
                .collect(Collectors.toList()))
            .containsExactlyInAnyOrder("999999999", "888888888");

        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(meldingRepository.hentMedAltinnStatus(AltinnStatus.IKKE_SENDT)).isEmpty();
            assertThat(meldingRepository.hentMedAltinnStatus(AltinnStatus.OK)
                    .stream()
                    .map(p -> p.getOrgnr())
                    .collect(Collectors.toList()))
                    .containsExactlyInAnyOrder("999999999", "888888888");
            assertThat(meldingRepository.hentMedStatus(AltinnStatus.OK, JoarkStatus.OK)
                    .stream()
                    .map(p -> p.getOrgnr())
                    .collect(Collectors.toList()))
                    .containsExactlyInAnyOrder("999999999", "888888888");
            assertThat(meldingRepository.hentMedStatus(AltinnStatus.OK, JoarkStatus.OK)
                    .stream()
                    .map(p -> p.getTema())
                    .collect(Collectors.toList()))
                    .containsExactlyInAnyOrder(JoarkTema.PER, JoarkTema.PER);
        });

    }

    @Test
    public void api__skal_kun_akseptere_et_set_av_temaer() throws Exception {
        String melding = "{\n" +
                "\"organisasjonsnumre\": [ \"12345\" ],\n" +
                "\"melding\": \"string\",\n" +
                "\"tittel\": \"string\",\n" +
                "\"systemUsercode\": \"string\",\n" +
                "\"serviceCode\": \"string\",\n" +
                "\"serviceEdition\": \"string\",\n" +
                "\"tillatAutomatiskSlettingFraDato\": \"2021-02-17T13:52:22.206Z\",\n" +
                "\"tillatAutomatiskSlettingEtterAntallÅr\": 0,\n" +
                "\"tema\": \"WOOP\",\n" +
                "\"vedlegg\": [ ]\n" +
                "}";

        HttpResponse<String> response = newBuilder().build().send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + webAppPort + "/altinn-meldinger-api/melding"))
                        .header("Content-Type", "application/json")
                        .header("idempotency-key", Ulider.nextULID())
                        .header("Authorization", "Bearer " + TestUtils.token(mockOAuth2Server, "aad", "subject", "altinn-meldinger-api", "rettighet-for-å-bruke-apiet-lokalt"))
                        .POST(HttpRequest.BodyPublishers.ofString(melding))
                        .build(),
                ofString()
        );
    }

    @Test
    public void api__skal_autentisere_bruker() throws Exception {
        HttpResponse<String> response = newBuilder().build().send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + webAppPort + "/altinn-meldinger-api/melding"))
                        .POST(HttpRequest.BodyPublishers.ofString("{}"))
                        .build(),
                ofString()
        );
        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    public void api__skal_autorisere_bruker() throws Exception {
        HttpResponse<String> response = newBuilder().build().send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + webAppPort + "/altinn-meldinger-api/melding"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + TestUtils.token(mockOAuth2Server, "aad", "subject", "altinn-meldinger-api", "feilgruppe"))
                        .header("idempotency-key", Ulider.nextULID())
                        .POST(HttpRequest.BodyPublishers.ofString("{}"))
                        .build(),
                ofString()
        );
        assertThat(response.statusCode()).isEqualTo(403);
    }

}
