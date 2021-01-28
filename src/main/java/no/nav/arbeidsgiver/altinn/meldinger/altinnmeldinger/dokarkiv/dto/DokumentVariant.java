package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto;

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

    public static String getFiltypePdf() {
        return FILTYPE_PDF;
    }

    public static String getFiltypeJson() {
        return FILTYPE_JSON;
    }

    public static String getVARIANFORMAT() {
        return VARIANFORMAT;
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
