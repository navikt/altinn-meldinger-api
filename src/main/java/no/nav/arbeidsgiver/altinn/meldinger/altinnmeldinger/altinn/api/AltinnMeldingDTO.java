package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Melding;

import java.time.LocalDateTime;
import java.util.List;

public class AltinnMeldingDTO {

    private final List<String> organisasjonsnumre;
    private final String melding;
    private final String tittel;

    private final String systemUsercode;
    private final String serviceCode;
    private final String serviceEdition;
    // private final String externalShipmentReference; Ta inn correlation ID som header i stedet

    private final LocalDateTime tillatAutomatiskSlettingFraDato;
    private final Integer tillatAutomatiskSlettingEtterAntallÅr;
    private final JoarkTema tema;
    private final List<PdfVedleggDTO> vedlegg;

    public AltinnMeldingDTO(List<String> orgnr, String melding, String tittel, String systemUsercode, String serviceCode, String serviceEdition, LocalDateTime tillatAutomatiskSlettingFraDato, int tillatAutomatiskSlettingEtterAntallÅr, JoarkTema tema, List<PdfVedleggDTO> vedlegg) {
        this.organisasjonsnumre = orgnr;
        this.melding = melding;
        this.tittel = tittel;
        this.systemUsercode = systemUsercode;
        this.serviceCode = serviceCode;
        this.serviceEdition = serviceEdition;
        this.tillatAutomatiskSlettingFraDato = tillatAutomatiskSlettingFraDato;
        this.tillatAutomatiskSlettingEtterAntallÅr = tillatAutomatiskSlettingEtterAntallÅr;
        this.tema = tema;
        this.vedlegg = vedlegg;
    }

    public List<String> getOrganisasjonsnumre() {
        return organisasjonsnumre;
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

    public JoarkTema getTema() { return tema; }

    public List<PdfVedleggDTO> getVedlegg() { return vedlegg; }

    public Melding tilMelding() {
        return new Melding(organisasjonsnumre, melding, tittel, systemUsercode, serviceCode, serviceEdition, tillatAutomatiskSlettingFraDato, tillatAutomatiskSlettingEtterAntallÅr, tema, PdfVedleggDTO.tilVedleggListe(vedlegg));
    }
}
