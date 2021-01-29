package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Melding;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JournalpostMapper {

    private static final Logger log = LoggerFactory.getLogger(JournalpostMapper.class);

    public Journalpost meldingTilJournalpost(Melding melding) {
        try {
            return opprettJournalpost(melding);
        } catch (Exception e) {
            log.error("Feil ved mapping til Journalpost", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Journalpost opprettJournalpost(Melding melding) {
        Bruker bruker = new Bruker(melding.getOrgnr());
        Mottaker mottaker = new Mottaker(melding.getOrgnr());

        List<Dokument> dokumenter = melding.getVedlegg()
                .stream()
                .map(pdfVedlegg -> new Dokument(pdfVedlegg.getVedleggnavn(), Arrays.asList(new DokumentVariant(pdfVedlegg.getFilinnhold()))))
                .collect(Collectors.toList());

        return new Journalpost(melding.getTittel(), bruker, mottaker, dokumenter);
    }
}