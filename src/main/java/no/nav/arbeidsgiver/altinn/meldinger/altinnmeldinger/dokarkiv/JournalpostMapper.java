package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.PdfGenClient;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.MeldingsProsessering;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JournalpostMapper {

    private static final Logger log = LoggerFactory.getLogger(JournalpostMapper.class);
    private final PdfGenClient pdfGenClient;

    public JournalpostMapper(PdfGenClient pdfGenClient) {
        this.pdfGenClient = pdfGenClient;
    }

    public Journalpost meldingTilJournalpost(MeldingsProsessering melding) {
        try {
            byte[] meldingPdf = opprettHovedDokument(meldingTilJson(melding.getMelding()));
            return opprettJournalpost(meldingPdf, melding);
        } catch (Exception e) {
            log.error("Feil ved mapping til Journalpost", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private byte[] opprettHovedDokument(String jsonMelding) {
        return pdfGenClient.hovedmeldingPdfBytes(jsonMelding);
    }

    private String meldingTilJson(String melding) {
        List<String> meldinger = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        Document doc = Jsoup.parse(melding);

        builder.append("{\"melding\": \"").append(doc.text()).append("\"}");
        meldinger.add(builder.toString());

        Elements links = doc.select("a[href]");
        links.stream().forEach(link -> {
            builder.delete(0, builder.length());
            builder.append("{\"melding\": \"")
                    .append(link.text())
                    .append(": ")
                    .append(link.attr("href")).append("\"}");
            meldinger.add(builder.toString());
        });

        builder.delete(0, builder.length());
        builder.append("{ \"meldinger\":").append(meldinger.toString()).append("}");
        return builder.toString();
    }

    private Journalpost opprettJournalpost(byte[] meldingPdf, MeldingsProsessering melding) {
        Bruker bruker = new Bruker(melding.getOrgnr());
        Mottaker mottaker = new Mottaker(melding.getOrgnr());

        List<Dokument> dokumenter = melding.getVedlegg()
                .stream()
                .map(pdfVedlegg -> new Dokument(pdfVedlegg.getVedleggnavn(), Arrays.asList(new DokumentVariant(pdfVedlegg.getFilinnhold()))))
                .collect(Collectors.toList());

        dokumenter.add(0, new Dokument(melding.getTittel(), Arrays.asList(new DokumentVariant(meldingPdf.toString()))));
        return new Journalpost(melding.getTittel(), bruker, mottaker, dokumenter);
    }
}