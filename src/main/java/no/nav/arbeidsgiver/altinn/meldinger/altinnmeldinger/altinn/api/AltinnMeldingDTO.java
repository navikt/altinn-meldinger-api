package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Melding;

import java.time.LocalDateTime;
import java.util.List;

public class AltinnMeldingDTO {

    private final String orgnr;
    private final String melding;
    private final String tittel;

    private final String systemUsercode;
    private final String serviceCode;
    private final String serviceEdition;
    // private final String externalShipmentReference; Ta inn correlation ID som header i stedet

    private final LocalDateTime tillatAutomatiskSlettingFraDato;
    private final Integer tillatAutomatiskSlettingEtterAntallÅr;
    private final List<PdfVedleggDTO> vedlegg;

    public AltinnMeldingDTO(String orgnr, String melding, String tittel, String systemUsercode, String serviceCode, String serviceEdition, LocalDateTime tillatAutomatiskSlettingFraDato, int tillatAutomatiskSlettingEtterAntallÅr, List<PdfVedleggDTO> vedlegg) {
        this.orgnr = orgnr;
        this.melding = melding;
        this.tittel = tittel;
        this.systemUsercode = systemUsercode;
        this.serviceCode = serviceCode;
        this.serviceEdition = serviceEdition;
        this.tillatAutomatiskSlettingFraDato = tillatAutomatiskSlettingFraDato;
        this.tillatAutomatiskSlettingEtterAntallÅr = tillatAutomatiskSlettingEtterAntallÅr;
        this.vedlegg = vedlegg;
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

    public List<PdfVedleggDTO> getVedlegg() { return vedlegg; }

    public Melding toMeldingLogg() {
        return new Melding(orgnr, melding, tittel, systemUsercode, serviceCode, serviceEdition, tillatAutomatiskSlettingFraDato, tillatAutomatiskSlettingEtterAntallÅr, PdfVedleggDTO.tilVedleggListe(vedlegg));
    }
}
