package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Ulider;

import java.time.LocalDateTime;
import java.util.List;

public class Melding {

    private String id;
    private LocalDateTime opprettet;
    private String orgnr;
    private String melding;
    private String tittel;
    private String systemUsercode;
    private String serviceCode;
    private String serviceEdition;
    private LocalDateTime tillatAutomatiskSlettingFraDato;
    private Integer tillatAutomatiskSlettingEtterAntallÅr;
    private List<Vedlegg> vedlegg;
    private AltinnStatus altinnStatus;
    private String altinnReferanse;
    private LocalDateTime altinnSendtTidspunkt;

    public Melding(String orgnr, String melding, String tittel, String systemUsercode, String serviceCode, String serviceEdition, LocalDateTime tillatAutomatiskSlettingFraDato, Integer tillatAutomatiskSlettingEtterAntallÅr, List<Vedlegg> vedlegg) {
        this.opprettet = LocalDateTime.now();
        this.id = Ulider.nextULID();
        this.altinnStatus = AltinnStatus.IKKE_SENDT;
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

    protected Melding() {
    };

    public Melding(LocalDateTime opprettet, String id, String orgnr, String melding, String tittel, String systemUsercode, String serviceCode, String serviceEdition, LocalDateTime tillatAutomatiskSlettingFraDato, Integer tillatAutomatiskSlettingEtterAntallÅr, AltinnStatus altinnStatus, List<Vedlegg> vedlegg, String altinnReferanse, LocalDateTime altinnSendtTidspunkt) {
        this.opprettet = opprettet;
        this.id = id;
        this.orgnr = orgnr;
        this.melding = melding;
        this.tittel = tittel;
        this.systemUsercode = systemUsercode;
        this.serviceCode = serviceCode;
        this.serviceEdition = serviceEdition;
        this.tillatAutomatiskSlettingFraDato = tillatAutomatiskSlettingFraDato;
        this.tillatAutomatiskSlettingEtterAntallÅr = tillatAutomatiskSlettingEtterAntallÅr;
        this.altinnStatus = altinnStatus;
        this.vedlegg = vedlegg;
        this.altinnReferanse = altinnReferanse;
        this.altinnSendtTidspunkt = altinnSendtTidspunkt;
    }

    public void setStatus(AltinnStatus altinnStatus) {
        this.altinnStatus = altinnStatus;
    }

    public AltinnStatus getAltinnStatus() {
        return altinnStatus;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getOpprettet() {
        return opprettet;
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

    public List<Vedlegg> getVedlegg() {
        return vedlegg;
    }

    public String getAltinnReferanse() { return altinnReferanse; }

    public LocalDateTime getAltinnSendtTidspunkt() { return altinnSendtTidspunkt; }

}
