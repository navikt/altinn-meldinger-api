package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.JoarkTema;

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
    private JoarkStatus joarkStatus;
    private String journalpostId;
    private LocalDateTime joarkSendtTidspunkt;
    private JoarkTema tema;

    public MeldingsProsessering(String id, String meldingId, String orgnr, String melding, String tittel, String systemUsercode, String serviceCode, String serviceEdition, LocalDateTime tillatAutomatiskSlettingFraDato, Integer tillatAutomatiskSlettingEtterAntallÅr, AltinnStatus altinnStatus, List<Vedlegg> vedlegg, String altinnReferanse, LocalDateTime altinnSendtTidspunkt, JoarkStatus joarkStatus, String journalpostId, LocalDateTime joarkSendtTidspunkt, JoarkTema tema) {
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
        this.joarkStatus = joarkStatus;
        this.journalpostId = journalpostId;
        this.joarkSendtTidspunkt = joarkSendtTidspunkt;
        this.tema = tema;
    }

    public String getId() {
        return id;
    }

    public String getMeldingId() {
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

    public JoarkStatus getJoarkStatus() {
        return joarkStatus;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public LocalDateTime getJoarkSendtTidspunkt() {
        return joarkSendtTidspunkt;
    }

    public JoarkTema getTema() {
        return tema;
    }
}
