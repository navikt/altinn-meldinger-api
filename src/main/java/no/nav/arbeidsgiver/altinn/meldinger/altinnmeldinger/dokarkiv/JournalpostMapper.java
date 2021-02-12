package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.PdfGenClient;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.MeldingsProsessering;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.MeldingTilPdfJson.opprettPdfJson;

@Component
public class JournalpostMapper {

    private static final Logger log = LoggerFactory.getLogger(JournalpostMapper.class);
    private final PdfGenClient pdfGenClient;

    public JournalpostMapper(PdfGenClient pdfGenClient) {
        this.pdfGenClient = pdfGenClient;
    }

    public Journalpost meldingTilJournalpost(MeldingsProsessering melding) {
        try {
            byte[] meldingPdf = opprettHovedDokument(opprettPdfJson(melding.getMelding()));
            return opprettJournalpost(meldingPdf, melding);
        } catch (Exception e) {
            log.error("Feil ved mapping til Journalpost", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private byte[] opprettHovedDokument(String jsonMelding) {
        return pdfGenClient.hovedmeldingPdfBytes(jsonMelding);
    }

    private Journalpost opprettJournalpost(byte[] meldingPdf, MeldingsProsessering melding) {
        Bruker bruker = new Bruker(melding.getOrgnr());
        Mottaker mottaker = new Mottaker(melding.getOrgnr());

        List<Dokument> dokumenter = melding.getVedlegg()
                .stream()
                .map(pdfVedlegg -> new Dokument(pdfVedlegg.getVedleggnavn(), Arrays.asList(new DokumentVariant(pdfVedlegg.getFilinnhold()))))
                .collect(Collectors.toList());

        dokumenter.add(0, new Dokument(melding.getTittel(), Arrays.asList(new DokumentVariant(Base64.getEncoder().encodeToString(meldingPdf)))));
        return new Journalpost(melding.getTittel(), bruker, mottaker, dokumenter);
    }
}