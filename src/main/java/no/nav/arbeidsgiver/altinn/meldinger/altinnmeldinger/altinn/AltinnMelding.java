package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

public class AltinnMelding {
    public String getBedriftsnr() {
        return bedriftsnr;
    }

    public String getMelding() {
        return melding;
    }

    private final String bedriftsnr;
    private final String melding;

    public AltinnMelding(String bedriftsnr, String melding) {
        this.bedriftsnr = bedriftsnr;
        this.melding = melding;
    }
}
