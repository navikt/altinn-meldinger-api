package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Ulider;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class MeldingLogg {

    @Id
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
    @Enumerated(EnumType.STRING)
    private MeldingStatus status;

    private MeldingLogg(String orgnr, String melding, String tittel, String systemUsercode, String serviceCode, String serviceEdition, LocalDateTime tillatAutomatiskSlettingFraDato, Integer tillatAutomatiskSlettingEtterAntallÅr) {
        this.opprettet = LocalDateTime.now();
        this.id = Ulider.nextULID();
        this.orgnr = orgnr;
        this.melding = melding;
        this.tittel = tittel;
        this.systemUsercode = systemUsercode;
        this.serviceCode = serviceCode;
        this.serviceEdition = serviceEdition;
        this.tillatAutomatiskSlettingFraDato = tillatAutomatiskSlettingFraDato;
        this.tillatAutomatiskSlettingEtterAntallÅr = tillatAutomatiskSlettingEtterAntallÅr;
    }

    public static MeldingLogg from(AltinnMelding altinnMelding) {
        return new MeldingLogg(
                altinnMelding.getOrgnr(),
                altinnMelding.getMelding(),
                altinnMelding.getTittel(),
                altinnMelding.getSystemUsercode(),
                altinnMelding.getServiceCode(),
                altinnMelding.getServiceEdition(),
                altinnMelding.getTillatAutomatiskSlettingFraDato(),
                altinnMelding.getTillatAutomatiskSlettingEtterAntallÅr()
        );
    }

    protected MeldingLogg() {

    };

    public MeldingLogg(LocalDateTime opprettet, String id, String orgnr, String melding, String tittel, String systemUsercode, String serviceCode, String serviceEdition, LocalDateTime tillatAutomatiskSlettingFraDato, Integer tillatAutomatiskSlettingEtterAntallÅr, MeldingStatus status) {
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
        this.status = status;
    }

    public void setStatus(MeldingStatus status) {
        this.status = status;
    }

    public MeldingStatus getStatus() {
        return status;
    }
}
