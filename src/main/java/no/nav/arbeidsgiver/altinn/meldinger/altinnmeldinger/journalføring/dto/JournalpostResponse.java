package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.journalføring.dto;

public class JournalpostResponse {

    static final String ID_TYPE_ORGNR = "ORGNR";

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