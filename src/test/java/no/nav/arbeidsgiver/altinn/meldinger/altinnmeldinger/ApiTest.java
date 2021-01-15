package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"wiremock.port=8082"})
public class ApiTest {

    @LocalServerPort
    private String port;

    @Test
    public void api__skal_sende_melding_via_ws_og_returnere_created() throws Exception {
        HttpResponse<String> response = newBuilder().build().send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/altinn-meldinger-api/melding"))
                        .POST(HttpRequest.BodyPublishers.ofString("Dette er en melding som skal til Altinn"))
                        .build(),
                ofString()
        );

        assertThat(response.statusCode()).isEqualTo(201);
    }

}
