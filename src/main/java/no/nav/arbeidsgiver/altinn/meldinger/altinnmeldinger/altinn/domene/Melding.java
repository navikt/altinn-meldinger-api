package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Ulider;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.JoarkTema;

import java.time.LocalDateTime;
import java.util.List;

public class Melding {

    private String id;
    private List<String> organisasjoner;
    private String melding;
    private String tittel;
    private String systemUsercode;
    private String serviceCode;
    private String serviceEdition;
    private LocalDateTime tillatAutomatiskSlettingFraDato;
    private Integer tillatAutomatiskSlettingEtterAntallÅr;
    private JoarkTema tema;
    private List<Vedlegg> vedlegg;

    public Melding(List<String> orgnr, String melding, String tittel, String systemUsercode, String serviceCode, String serviceEdition, LocalDateTime tillatAutomatiskSlettingFraDato, Integer tillatAutomatiskSlettingEtterAntallÅr, JoarkTema tema, List<Vedlegg> vedlegg) {
        this.id = Ulider.nextULID();
        this.organisasjoner = orgnr;
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

    protected Melding() {
    };

    public Melding(String id, List<String> orgnr, String melding, String tittel, String systemUsercode, String serviceCode, String serviceEdition, LocalDateTime tillatAutomatiskSlettingFraDato, Integer tillatAutomatiskSlettingEtterAntallÅr, JoarkTema tema, List<Vedlegg> vedlegg) {
        this.id = id;
        this.organisasjoner = orgnr;
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

    public String getId() {
        return id;
    }

    public List<String> getOrganisasjoner() { return organisasjoner; }

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

    public List<Vedlegg> getVedlegg() {
        return vedlegg;
    }

    public JoarkTema getTema() { return tema; }
}
