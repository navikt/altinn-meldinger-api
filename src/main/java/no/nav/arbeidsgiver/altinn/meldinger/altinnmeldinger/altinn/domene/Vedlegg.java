package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Ulider;

public class Vedlegg {
    private String id;
    private String filinnhold;
    private String filnavn;
    private String vedleggnavn;

    public Vedlegg(String filinnhold, String filnavn, String vedleggnavn) {
        this.id = Ulider.nextULID();
        this.filinnhold = filinnhold;
        this.filnavn = filnavn;
        this.vedleggnavn = vedleggnavn;
    }

    public Vedlegg(String id, String filinnhold, String filnavn, String vedleggnavn) {
        this.id = id;
        this.filinnhold = filinnhold;
        this.filnavn = filnavn;
        this.vedleggnavn = vedleggnavn;
    }

    public Vedlegg() {
    }

    public String getId() {
        return id;
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
