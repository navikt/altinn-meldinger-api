package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.*;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.AltinnMeldingDTO;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.PdfVedleggDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;

import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"wiremock.port=8082"})
public class ApiTest {

    @LocalServerPort
    private String port;

    @Autowired
    private MeldingLoggRepository meldingLoggRepository;

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
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(altinnMelding)))
                        .header("Content-Type", "application/json")
                        .build(),
                ofString()
        );

        assertThat(response.statusCode()).isEqualTo(201);
        List<MeldingLogg> meldingsLoggRader = meldingLoggRepository.findAll();
        assertThat(meldingsLoggRader.size()).isEqualTo(1);
        assertThat(meldingsLoggRader.get(0).getAltinnStatus()).isEqualTo(AltinnStatus.OK);
    }

}
