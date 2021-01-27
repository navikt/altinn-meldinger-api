package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.journalføring.dto;

public class DokumentVariant {

    private static final String FILTYPE_PDF = "PDFA";
    private static final String FILTYPE_JSON = "JSON";
    private static final String VARIANFORMAT = "ARKIV";

    private final String filtype = FILTYPE_JSON;
    private final String variantformat = VARIANFORMAT;
    private final String fysiskDokument;

    public DokumentVariant(String fysiskDokument) {
        this.fysiskDokument = fysiskDokument;
    }
}
