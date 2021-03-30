package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto;

import java.util.List;

public class Journalpost {

    static final String ID_TYPE_ORGNR = "ORGNR";
    private final static String JOURNALPOST_TYPE = "UTGAAENDE";
    private final static String KANAL = "NAV_NO";
    // private final static String TEMA = "GEN";
    private final static String JOURNALFOERENDE_ENHET = "9999";

    private final String journalposttype = JOURNALPOST_TYPE;
    private final String kanal = KANAL;
    private final String tema;
    private final String journalfoerendeEnhet = JOURNALFOERENDE_ENHET;
    private final Mottaker avsenderMottaker;
    private final Bruker bruker;
    private final Sak sak = new Sak();
    private final String tittel;
    private final List<Dokument> dokumenter;

    public Journalpost(String tittel, Bruker bruker, Mottaker mottaker, String tema, List<Dokument> dokumenter) {
        this.avsenderMottaker = mottaker;
        this.bruker = bruker;
        this.dokumenter = dokumenter;
        this.tema = tema;
        this.tittel = tittel;
    }

    public Bruker getBruker() {
        return bruker;
    }

    public String getTema() {
        return tema;
    }

    public String getTittel() {
        return tittel;
    }

    public Sak getSak() {
        return sak;
    }

    public Mottaker getAvsenderMottaker() {
        return avsenderMottaker;
    }

    public List<Dokument> getDokumenter() {
        return dokumenter;
    }

    public String getJournalposttype() {
        return journalposttype;
    }

    public String getKanal() {
        return kanal;
    }

    public String getJournalfoerendeEnhet() {
        return journalfoerendeEnhet;
    }
}
