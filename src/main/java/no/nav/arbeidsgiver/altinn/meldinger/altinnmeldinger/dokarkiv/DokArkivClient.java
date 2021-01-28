package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingLogg;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.Journalpost;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.JournalpostResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class DokArkivClient {

    @Autowired
    private RestTemplate restempateDokArkivOauth2;

    @Autowired
    private JournalpostMapper journalpostMapper;

    private static final Logger log = LoggerFactory.getLogger(DokArkivClient.class);

    static final String QUERY_PARAM = "forsoekFerdigstill=true";
    private URI uri;
    //private final HttpHeaders headers = new HttpHeaders();

    public DokArkivClient(DokArkivConfig dokArkivConfig) {
        uri = UriComponentsBuilder.fromUri(dokArkivConfig.getUri())
                .query(QUERY_PARAM)
                .build()
                .toUri();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.setContentType((MediaType.APPLICATION_JSON));
    }

    public void journalførMelding(MeldingLogg meldingLogg) {
        sendJournalpost(journalpostMapper.meldingTilJournalpost(meldingLogg));
    }

    private String sendJournalpost(final Journalpost journalpost) {
        debugLogJournalpost(journalpost);
        try {
            return restempateDokArkivOauth2.postForObject(uri, new HttpEntity<>(journalpost), JournalpostResponse.class).getJournalpostId();
        } catch (Exception e) {
            log.error("Kall til Joark feilet", e);
            throw new RuntimeException("Kall til Joark feilet: " + e);
        }
    }

    private void debugLogJournalpost(Journalpost journalpost) {
        //  if (log.isDebugEnabled()) {
        try {
            log.info("JSON REQ: {}", new ObjectMapper().writeValueAsString(journalpost));
        } catch (JsonProcessingException e) {
            log.warn("PRINT FEILET");
        }
//        }
    }
}

