package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.journalføring.dto;

import java.util.List;

public class Dokument {
    private final String tittel;
    private final List<DokumentVariant> dokumentVarianter;

    public Dokument(String tittel, List<DokumentVariant> dokumentVarianter) {
        this.tittel = tittel;
        this.dokumentVarianter = dokumentVarianter;
    }
}
