package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingLogg;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.Dokument;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.DokumentVariant;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.Journalpost;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.Mottaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JournalpostMapper {

    private static final Logger log = LoggerFactory.getLogger(JournalpostMapper.class);

    public Journalpost meldingTilJournalpost(MeldingLogg meldingLogg) {
        try {
            return opprettJournalpost(meldingLogg);
        } catch (Exception e) {
            log.error("Feil ved mapping til Journalpost", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Journalpost opprettJournalpost(MeldingLogg meldingLogg) {
//        Sak sak = new Sak(opprettArkivsaknr(meldingLogg.get));
//        Bruker bruker = new Bruker(meldingLogg.getOrgnr());
        Mottaker mottaker = new Mottaker(meldingLogg.getOrgnr(), meldingLogg.getId()); //TODO orgNavn

        List<Dokument> dokumenter = meldingLogg.getVedlegg()
                .stream()
                .map(pdfVedlegg -> new Dokument(pdfVedlegg.getVedleggnavn(), Arrays.asList(new DokumentVariant(pdfVedlegg.getFilinnhold()))))
                .collect(Collectors.toList());

        return new Journalpost(mottaker, dokumenter);
    }
}