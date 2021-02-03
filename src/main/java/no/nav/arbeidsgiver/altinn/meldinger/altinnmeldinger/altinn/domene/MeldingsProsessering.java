package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene;

import java.time.LocalDateTime;
import java.util.List;

public class MeldingsProsessering {

    private String id;
    private String meldingId;
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

    public MeldingsProsessering(String id, String meldingId, String orgnr, String melding, String tittel, String systemUsercode, String serviceCode, String serviceEdition, LocalDateTime tillatAutomatiskSlettingFraDato, Integer tillatAutomatiskSlettingEtterAntallÅr, AltinnStatus altinnStatus, List<Vedlegg> vedlegg, String altinnReferanse, LocalDateTime altinnSendtTidspunkt) {
        this.id = id;
        this.meldingId = meldingId;
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

    public String getId() {
        return id;
    }

    public String getMeldingsId() {
        return meldingId;
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

    public AltinnStatus getAltinnStatus() {
        return altinnStatus;
    }

    public String getAltinnReferanse() {
        return altinnReferanse;
    }

    public LocalDateTime getAltinnSendtTidspunkt() {
        return altinnSendtTidspunkt;
    }
}