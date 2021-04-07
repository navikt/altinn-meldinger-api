package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Ulider;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api.JoarkTema;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
            JoarkTema.PER,
            List.of(
                    new Vedlegg("A1", "filinnhold1", "filnavn1", "vedleggnavn1"),
                    new Vedlegg("A2", "filinnhold2", "filnavn2", "vedleggnavn2"),
                    new Vedlegg("A3", "filinnhold3", "filnavn3", "vedleggnavn3")
            ));

    public static final Melding TEST_MELDING_2 = new Melding(
            "A2",
            List.of("111111111", "222222222"),
            "Melding",
            "Tittel",
            "systemUserCode",
            "serviceCode",
            "serviceEdition",
            LocalDateTime.now().plusYears(2),
            0,
            JoarkTema.PER,
            List.of(
                    new Vedlegg("A4", "filinnhold1", "filnavn1", "vedleggnavn1"),
                    new Vedlegg("A5", "filinnhold2", "filnavn2", "vedleggnavn2"),
                    new Vedlegg("A6", "filinnhold3", "filnavn3", "vedleggnavn3")
            ));

    @Test
    public void hentProsesseringsstatus_metoder_skal_ta_hensyn_til_statuser() {
        meldingRepository.opprett(TEST_MELDING, Ulider.nextULID());

        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilAltinn().size()).isEqualTo(2);
        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilDokarkiv().size()).isEqualTo(0);

        meldingRepository.hentMeldingerSomSkalSendesTilAltinn().forEach(
                meldingsProsessering -> meldingRepository.oppdaterAltinnStatus(meldingsProsessering.getId(), AltinnStatus.OK, "altinnref"));

        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilAltinn().size()).isEqualTo(0);
        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilDokarkiv().size()).isEqualTo(2);

        meldingRepository.hentMeldingerSomSkalSendesTilDokarkiv().forEach(
                meldingsProsessering -> meldingRepository.oppdaterDokarkivStatus(meldingsProsessering.getId(), JoarkStatus.OK, "joarkref"));

        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilAltinn().size()).isEqualTo(0);
        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilDokarkiv().size()).isEqualTo(0);

    }

    @Test
    public void hentProsesseringsstatus_metoder_skal_bygge_ett_objekt_pr_prosesseringsrad() {
        meldingRepository.opprett(TEST_MELDING, Ulider.nextULID());

        List<MeldingsProsessering> prosesseringList = meldingRepository.hentMedStatus(AltinnStatus.IKKE_SENDT, JoarkStatus.IKKE_SENDT);
        assertThat(prosesseringList.size()).isEqualTo(2);
        // Verifiser at det er ett prosesseringsobjekt pr. orgnummer og at begge har tre vedlegg
        assertThat((int) prosesseringList.stream().filter(p -> "111111111".equals(p.getOrgnr())).count()).isEqualTo(1);
        assertThat((int) prosesseringList.stream().filter(p -> "222222222".equals(p.getOrgnr())).count()).isEqualTo(1);
        assertThat((int) prosesseringList.stream().filter(p -> p.getVedlegg().size() == 3).count()).isEqualTo(2);

        prosesseringList.forEach(meldingsProsessering -> {
            meldingRepository.oppdaterAltinnStatus(meldingsProsessering.getId(), AltinnStatus.OK, "altinnref");
            meldingRepository.oppdaterDokarkivStatus(meldingsProsessering.getId(), JoarkStatus.OK, "journalpost");
        });

        prosesseringList = meldingRepository.hentMedStatus(AltinnStatus.OK, JoarkStatus.OK);
        // Verifiser at objektene er oppdatert med status, altinnreferanse og joarkreferanse
        assertThat(prosesseringList.size()).isEqualTo(2);
        assertThat((int) prosesseringList.stream().filter(p -> "altinnref".equals(p.getAltinnReferanse())).count()).isEqualTo(2);
        assertThat((int) prosesseringList.stream().filter(p -> "journalpost".equals(p.getJournalpostId())).count()).isEqualTo(2);

    }

    @Test
    public void opprett_melding_skal_ha_unik_idempotency_feil_hvis_ikke() {
        String ulid = Ulider.nextULID();
        meldingRepository.opprett(TEST_MELDING, ulid);
        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilAltinn().size()).isEqualTo(2);
        assertThatThrownBy(() -> {
            meldingRepository.opprett(TEST_MELDING_2, ulid);
        }).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    public void opprett_melding_skal_ha_unik_idempotency() {
        meldingRepository.opprett(TEST_MELDING, Ulider.nextULID());
        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilAltinn().size()).isEqualTo(2);
        meldingRepository.opprett(TEST_MELDING_2, Ulider.nextULID());
        assertThat(meldingRepository.hentMeldingerSomSkalSendesTilAltinn().size()).isEqualTo(4);
    }

    @Test
    public void prosessering_og_parsing_av_domeneobjeckter() {
        meldingRepository.opprett(TEST_MELDING, Ulider.nextULID());
        List<MeldingsProsessering> prosesseringList = meldingRepository.hentMedStatus(AltinnStatus.IKKE_SENDT, JoarkStatus.IKKE_SENDT);
        List<JoarkTema> temas = prosesseringList.stream().map(p -> p.getTema()).collect(Collectors.toList());

    }
}
