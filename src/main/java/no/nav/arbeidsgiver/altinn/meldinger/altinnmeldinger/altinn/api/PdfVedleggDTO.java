package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.PdfVedlegg;

import java.util.List;
import java.util.stream.Collectors;

public class PdfVedleggDTO {
    private final String filinnhold;
    private final String filnavn;
    private final String vedleggnavn;

    public PdfVedleggDTO(String filinnhold, String filnavn, String vedleggnavn) {
        this.filinnhold = filinnhold;
        this.filnavn = filnavn;
        this.vedleggnavn = vedleggnavn;
    }

    public static List<PdfVedlegg> tilVedleggListe(List<PdfVedleggDTO> vedlegg) {
        return vedlegg.stream().map(PdfVedleggDTO::tilVedlegg).collect(Collectors.toList());
    }

    private PdfVedlegg tilVedlegg() {
        return new PdfVedlegg(filinnhold, filnavn, vedleggnavn);
    }

    public String getFilnavn() {
        return filnavn;
    }

    public String getVedleggnavn() {
        return vedleggnavn;
    }

    public String getFilinnhold() {
        return filinnhold;
    }
}
