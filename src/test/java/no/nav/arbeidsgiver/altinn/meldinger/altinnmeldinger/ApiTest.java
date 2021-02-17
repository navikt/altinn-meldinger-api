package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.AltinnMeldingDTO;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.PdfVedleggDTO;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.AltinnStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.JoarkStatus;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LokalApplikasjon.class)
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
                vedlegg);

        HttpResponse<String> response = newBuilder().build().send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/altinn-meldinger-api/melding"))
                        .header("Authorization", "Bearer " + token("aad", "subject", "audience", ""))
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(altinnMelding)))
                        .header("Content-Type", "application/json")
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
        });

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"feilgruppe"})
    public void api__skal_validere_token(String gruppe) throws Exception {
        HttpResponse<String> response = newBuilder().build().send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/altinn-meldinger-api/protected"))
                        .GET()
                        .header("Authorization", "Bearer " + token("aad", "subject", "audience", gruppe))
                        .build(),
                ofString()
        );

        assertThat(response.statusCode()).isEqualTo(403);
    }

    private String token(String issuerId, String subject, String audience, String... groups) {
        return mockOAuth2Server.issueToken(
                issuerId,
                "theclientid",
                new DefaultOAuth2TokenCallback(
                        issuerId,
                        subject,
                        audience,
                        Collections.singletonMap("groups", Arrays.asList(groups)),
                        3600
                )
        ).serialize();
    }

}
