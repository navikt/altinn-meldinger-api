package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto;

import static no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.Journalpost.ID_TYPE_ORGNR;

public class JournalpostResponse {

    private String journalpostId;
    private String journalstatus;
    private String melding;

    public static String getIdTypeOrgnr() {
        return ID_TYPE_ORGNR;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public String getJournalstatus() {
        return journalstatus;
    }

    public String getMelding() {
        return melding;
    }
}