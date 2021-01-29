package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto;

import static no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.Journalpost.ID_TYPE_ORGNR;

public class Mottaker {

    private final static String NA = "N/A";
    private final String idType = ID_TYPE_ORGNR;
    private final String id;
    private final String navn = NA;

    public Mottaker(String id) {
        this.id = id;
    }

    public String getIdType() {
        return idType;
    }

    public String getId() {
        return id;
    }

    public String getNavn() {
        return navn;
    }
}
