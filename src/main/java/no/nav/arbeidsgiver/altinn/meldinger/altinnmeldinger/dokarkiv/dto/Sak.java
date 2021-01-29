package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto;

public class Sak {
    private static final String TYPE_SAK = "GENERELL_SAK";
    private final String sakstype = TYPE_SAK;

    public String getSakstype() {
        return sakstype;
    }
}
