package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import java.time.LocalDateTime;

public class AltinnMelding {

    private final String orgnr;
    private final String melding;
    private final String tittel;

    private final String systemUsercode;
    private final String serviceCode;
    private final String serviceEdition;
    // private final String externalShipmentReference; Ta inn correlation ID som header i stedet

    private final LocalDateTime tillatAutomatiskSlettingFraDato;
    private final Integer tillatAutomatiskSlettingEtterAntallÅr;


    public AltinnMelding(String orgnr, String melding, String tittel, String systemUsercode, String serviceCode, String serviceEdition, LocalDateTime tillatAutomatiskSlettingFraDato, int tillatAutomatiskSlettingEtterAntallÅr) {
        this.orgnr = orgnr;
        this.melding = melding;
        this.tittel = tittel;
        this.systemUsercode = systemUsercode;
        this.serviceCode = serviceCode;
        this.serviceEdition = serviceEdition;
        this.tillatAutomatiskSlettingFraDato = tillatAutomatiskSlettingFraDato;
        this.tillatAutomatiskSlettingEtterAntallÅr = tillatAutomatiskSlettingEtterAntallÅr;
    }

    public String getOrgnr() {
        return orgnr;
    }

    public String getMelding() {
        return melding;
    }

    public String getTittel() {
        return tittel;
    }

    public String getSystemUsercode() {
        return systemUsercode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getServiceEdition() {
        return serviceEdition;
    }

    public LocalDateTime getTillatAutomatiskSlettingFraDato() {
        return tillatAutomatiskSlettingFraDato;
    }

    public Integer getTillatAutomatiskSlettingEtterAntallÅr() {
        return tillatAutomatiskSlettingEtterAntallÅr;
    }
}
