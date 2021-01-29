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

    public void setId(String id) {
        this.id = id;
    }

    public void setFilinnhold(String filinnhold) {
        this.filinnhold = filinnhold;
    }

    public void setFilnavn(String filnavn) {
        this.filnavn = filnavn;
    }

    public void setVedleggnavn(String vedleggnavn) {
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
