package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene;

public class PdfVedlegg {
    private final String filinnhold;
    private final String filnavn;
    private final String vedleggnavn;

    public PdfVedlegg(String filinnhold, String filnavn, String vedleggnavn) {
        this.filinnhold = filinnhold;
        this.filnavn = filnavn;
        this.vedleggnavn = vedleggnavn;
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
