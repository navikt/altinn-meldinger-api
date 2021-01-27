package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.journalføring.dto;

import java.util.List;

public class Journalpost {

    private final static String JOURNALPOST_TYPE = "UTGAAENDE";
    private final static String KANAL = "ALTINN";
    private final static String TEMA = ""; //TODO
    private final static String JOURNALFOERENDE_ENHET = "9999";

    public final static String TITTEL = "Tilsagnsbrev";

    private final String journalposttype = JOURNALPOST_TYPE;
    private final String kanal = KANAL;
    private final String tema = TEMA;
    private final String journalfoerendeEnhet = JOURNALFOERENDE_ENHET;

    private final List<Dokument> dokumenter;

    public Journalpost(List<Dokument> dokumenter) {
        this.dokumenter = dokumenter;
    }
}
