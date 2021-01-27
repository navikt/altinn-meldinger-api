package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.journalføring;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.journalføring.dto.Journalpost;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.journalføring.dto.JournalpostResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

@Component
public class JoarkClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(JoarkClient.class);
    static final String PATH = "/rest/journalpostapi/v1/journalpost";
    static final String QUERY_PARAM = "forsoekFerdigstill=true";
    private URI uri;
    private final HttpHeaders headers = new HttpHeaders();

    public JoarkClient(JoarkKonfig joarkKonfig) {
        uri = UriComponentsBuilder.fromUri(joarkKonfig.getUri())
                .path(PATH)
                .query(QUERY_PARAM)
                .build()
                .toUri();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType((MediaType.APPLICATION_JSON));
    }

    public String sendJournalpost(final Journalpost journalpost) {
        try {
            return restTemplate.postForObject(uri, new HttpEntity<>(journalpost, headers), JournalpostResponse.class).getJournalpostId();
        } catch (Exception e1) {
            log.warn("Feil ved kommunikasjon mot journalpost-API. Henter nytt sts-token og forsøker igjen");
            try {
                return restTemplate.postForObject(uri, entityMedStsToken(journalpost), JournalpostResponse.class).getJournalpostId();
            } catch (Exception e2) {
                log.error("Kall til Joark feilet", e2);
                throw new RuntimeException("Kall til Joark feilet: " + e2);
            }
        }
    }

    private HttpEntity<Journalpost> entityMedStsToken(final Journalpost journalpost) {

        HttpEntity<Journalpost> entity = new HttpEntity<>(journalpost, headers);
        return entity;
    }
}
