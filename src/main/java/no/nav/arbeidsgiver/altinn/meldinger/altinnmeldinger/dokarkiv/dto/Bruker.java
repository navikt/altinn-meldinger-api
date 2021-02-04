package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto;

import static no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.Journalpost.ID_TYPE_ORGNR;

public class Bruker {

    private final String idType = ID_TYPE_ORGNR;
    private final String id;

    public Bruker(String id) {
        this.id = id;
    }

    public String getIdType() {
        return idType;
    }

    public String getId() {
        return id;
    }
}
