package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto;

public class DokumentVariant {

    private static final String FILTYPE_PDF = "PDFA";
    private static final String VARIANFORMAT = "ARKIV";

    private final String filtype = FILTYPE_PDF;
    private final String variantformat = VARIANFORMAT;
    private final String fysiskDokument;

    public DokumentVariant(String fysiskDokument) {
        this.fysiskDokument = fysiskDokument;
    }

    public String getFiltype() {
        return filtype;
    }

    public String getVariantformat() {
        return variantformat;
    }

    public String getFysiskDokument() {
        return fysiskDokument;
    }
}
