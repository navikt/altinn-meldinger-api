package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

public class AltinnMelding {

    private final String bedriftsnr;
    private final String melding;
    private final String tittel;

    public AltinnMelding(String bedriftsnr, String melding, String tittel) {
        this.bedriftsnr = bedriftsnr;
        this.melding = melding;
        this.tittel = tittel;
    }

    public String getBedriftsnr() {
        return bedriftsnr;
    }

    public String getMelding() {
        return melding;
    }

    public String getTittel() {
        return tittel;
    }

}
