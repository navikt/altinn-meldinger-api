package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.AltinnMeldingDTO;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.PdfVedleggDTO;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.AltinnStatus;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LokalApplikasjon.class)
@TestPropertySource(properties = {
        "wiremock.port=8088",
        "altinn.uri=http://localhost:${wiremock.port}/altinn/failure"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@EnableMockOAuth2Server
@ActiveProfiles("test")
public class ApiAltinnServerErrorTest {

    @LocalServerPort
    private String port;

    @Autowired
    private MeldingRepository meldingRepository;

    @Autowired
    private MockOAuth2Server mockOAuth2Server;

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
                vedlegg);

        HttpResponse<String> response = newBuilder().build().send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/altinn-meldinger-api/melding"))
                        .header("Content-Type", "application/json")
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
