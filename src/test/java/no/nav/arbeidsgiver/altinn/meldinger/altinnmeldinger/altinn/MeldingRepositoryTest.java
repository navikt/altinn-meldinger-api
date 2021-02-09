package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ContextConfiguration(classes = MeldingRepository.class)
class MeldingRepositoryTest {

    @Autowired
    private MeldingRepository meldingRepository;

    public static final Melding TEST_MELDING = new Melding(
            "A1",
            List.of("111111111", "222222222"),
            "Melding",
            "Tittel",
            "systemUserCode",
            "serviceCode",
            "serviceEdition",
            LocalDateTime.now().plusYears(2),
            0,
            List.of(
                    new Vedlegg("A1", "filinnhold1", "filnavn1", "vedleggnavn1"),
                    new Vedlegg("A2", "filinnhold2", "filnavn2", "vedleggnavn2"),
                    new Vedlegg("A3", "filinnhold3", "filnavn3", "vedleggnavn3")
            ));

    @Test
    public void hentProsesseringsstatus_metoder_skal_ta_hensyn_til_statuser() {
        meldingRepository.opprett(TEST_MELDING);

        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilAltinn().size()).isEqualTo(2);
        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilDokarkiv().size()).isEqualTo(0);

        meldingRepository.hentMeldingerSomSkalSendesTilAltinn().forEach(
                meldingsProsessering -> meldingRepository.oppdaterAltinnStatus(meldingsProsessering.getId(), AltinnStatus.OK, "altinnref"));

        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilAltinn().size()).isEqualTo(0);
        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilDokarkiv().size()).isEqualTo(2);

        meldingRepository.hentMeldingerSomSkalSendesTilDokarkiv().forEach(
                meldingsProsessering -> meldingRepository.oppdaterJournalpostId(meldingsProsessering.getId(), JoarkStatus.OK, "joarkref"));

        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilAltinn().size()).isEqualTo(0);
        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilDokarkiv().size()).isEqualTo(0);

    }

    @Test
    public void hentProsesseringsstatus_metoder_skal_bygge_ett_objekt_pr_prosesseringsrad() {
        meldingRepository.opprett(TEST_MELDING);

        List<MeldingsProsessering> prosesseringList = meldingRepository.hentMedStatus(AltinnStatus.IKKE_SENDT, JoarkStatus.IKKE_SENDT);
        assertThat(prosesseringList.size()).isEqualTo(2);
        // Verifiser at det er ett prosesseringsobjekt pr. orgnummer og at begge har tre vedlegg
        assertThat((int) prosesseringList.stream().filter(p -> "111111111".equals(p.getOrgnr())).count()).isEqualTo(1);
        assertThat((int) prosesseringList.stream().filter(p -> "222222222".equals(p.getOrgnr())).count()).isEqualTo(1);
        assertThat((int) prosesseringList.stream().filter(p -> p.getVedlegg().size() == 3).count()).isEqualTo(2);

        prosesseringList.forEach(meldingsProsessering -> {
            meldingRepository.oppdaterAltinnStatus(meldingsProsessering.getId(), AltinnStatus.OK, "altinnref");
            meldingRepository.oppdaterJournalpostId(meldingsProsessering.getId(), JoarkStatus.OK, "journalpost");
        });

        prosesseringList = meldingRepository.hentMedStatus(AltinnStatus.OK, JoarkStatus.OK);
        // Verifiser at objektene er oppdatert med status, altinnreferanse og joarkreferanse
        assertThat(prosesseringList.size()).isEqualTo(2);
        assertThat((int) prosesseringList.stream().filter(p -> "altinnref".equals(p.getAltinnReferanse())).count()).isEqualTo(2);
        assertThat((int) prosesseringList.stream().filter(p -> "journalpost".equals(p.getJournalpostId())).count()).isEqualTo(2);

    }

}