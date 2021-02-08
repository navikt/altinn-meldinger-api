package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.DokArkivConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;

@Service
public class PdfGenClient {

    private static final Logger log = LoggerFactory.getLogger(PdfGenClient.class);

    @Autowired
    private RestTemplate restTemplateOauth2;

    private HttpHeaders headers = new HttpHeaders();
    private URI uri;

    public PdfGenClient(DokArkivConfig dokArkivConfig) {
        uri = UriComponentsBuilder.fromUriString(dokArkivConfig.getPdfGenUri())
                .build()
                .toUri();
        headers.setContentType(new MediaType(MediaType.APPLICATION_JSON, Charset.defaultCharset()));
    }

    public byte[] hovedmeldingPdfBytes(String meldingJson) {
        return hentPdf(meldingJson);
    }

    private byte[] hentPdf(String meldingJson) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(meldingJson, headers);
            return restTemplateOauth2.postForObject(uri, entity, byte[].class);
        } catch (Exception e) {
            log.error("Feil ved oppretting av pdf fil", e);
            throw new RuntimeException(e.getMessage());
        }
    }
}


