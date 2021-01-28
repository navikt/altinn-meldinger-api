package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto;

import java.util.List;

public class Journalpost {

    static final String ID_TYPE_ORGNR = "ORGNR";
    private final static String JOURNALPOST_TYPE = "UTGAAENDE";
    private final static String KANAL = "NAV_NO";
    private final static String TEMA = ""; //TODO
    private final static String JOURNALFOERENDE_ENHET = "9999";

    public final static String TITTEL = "";

    private final String journalposttype = JOURNALPOST_TYPE;
    private final String kanal = KANAL;
    private final String tema = TEMA;
    private final String journalfoerendeEnhet = JOURNALFOERENDE_ENHET;
    private final Mottaker avsenderMottaker;

    private final List<Dokument> dokumenter;

    public Journalpost(Mottaker mottaker, List<Dokument> dokumenter) {
        this.avsenderMottaker = mottaker;
        this.dokumenter = dokumenter;
    }

    public static String getIdTypeOrgnr() {
        return ID_TYPE_ORGNR;
    }

    public static String getJournalpostType() {
        return JOURNALPOST_TYPE;
    }

    public static String getKANAL() {
        return KANAL;
    }

    public static String getTEMA() {
        return TEMA;
    }

    public static String getJournalfoerendeEnhet() {
        return JOURNALFOERENDE_ENHET;
    }

    public Mottaker getAvsenderMottaker() {
        return avsenderMottaker;
    }

    public List<Dokument> getDokumenter() {
        return dokumenter;
    }

    public static String getTITTEL() {
        return TITTEL;
    }

    public String getJournalposttype() {
        return journalposttype;
    }

    public String getKanal() {
        return kanal;
    }

    public String getTema() {
        return tema;
    }
}
