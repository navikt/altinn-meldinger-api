package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Vedlegg;

import java.util.List;
import java.util.Optional;
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

    public static List<Vedlegg> tilVedleggListe(List<PdfVedleggDTO> vedlegg) {
        return Optional.ofNullable(vedlegg).orElse(List.of()).stream().map(PdfVedleggDTO::tilVedlegg).collect(Collectors.toList());
    }

    private Vedlegg tilVedlegg() {
        return new Vedlegg(filinnhold, filnavn, vedleggnavn);
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
