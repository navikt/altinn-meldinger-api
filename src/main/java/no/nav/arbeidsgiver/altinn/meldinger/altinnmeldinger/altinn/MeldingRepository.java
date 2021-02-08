package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Ulider;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.*;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableTransactionManagement
public class MeldingRepository {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private static final int ANTALL_PROSESSERINGS_RADER = 50;

    public MeldingRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_PROSESSERINGS_STATUS = "select " +
            "prosesserings_status.id, " +
            "prosesserings_status.melding_id, " +
            "prosesserings_status.orgnr, " +
            "melding.melding, " +
            "melding.tittel, " +
            "melding.system_usercode, " +
            "melding.service_code, " +
            "melding.service_edition, " +
            "melding.tillat_automatisk_sletting_fra_dato, " +
            "melding.tillat_automatisk_sletting_etter_antall_år, " +
            "prosesserings_status.altinn_status, " +
            "prosesserings_status.altinn_referanse, " +
            "prosesserings_status.altinn_sendt_tidspunkt, " +
            "prosesserings_status.joark_status, " +
            "prosesserings_status.journalpost_id, " +
            "prosesserings_status.joark_sendt_tidspunkt, " +
            "vedlegg.id as vedlegg_id, " +
            "vedlegg.filinnhold as vedlegg_filinnhold, " +
            "vedlegg.filnavn as vedlegg_filnavn, " +
            "vedlegg.vedleggnavn as vedlegg_vedleggnavn " +
            "from melding melding " +
            "join prosesserings_status prosesserings_status on prosesserings_status.melding_id = melding.id " +
            "left join vedlegg vedlegg on vedlegg.melding_id = melding.id ";

    private static final ResultSetExtractor<List<MeldingsProsessering>> MELDINGS_PROSESSERING_MAPPER =
            JdbcTemplateMapperFactory
                    .newInstance()
                    .addKeys("id", "vedlegg_id")
                    .newResultSetExtractor(MeldingsProsessering.class);

    public List<MeldingsProsessering> hentMedStatus(AltinnStatus altinnStatus, JoarkStatus joarkStatus) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("altinn_status", altinnStatus.name())
                .addValue("joark_status", joarkStatus.name())
                .addValue("antall", ANTALL_PROSESSERINGS_RADER);

        return jdbcTemplate.query(
                SELECT_PROSESSERINGS_STATUS +
                        "where prosesserings_status.id in (select distinct id from prosesserings_status where altinn_status = :altinn_status and joark_status = :joark_status order by id limit :antall) " +
                        "order by melding.id ",
                parameterSource,
                MELDINGS_PROSESSERING_MAPPER);
    }

    public List<MeldingsProsessering> hentMedAltinnStatus(AltinnStatus altinnStatus) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("altinn_status", altinnStatus.name())
                .addValue("antall", ANTALL_PROSESSERINGS_RADER);

        return jdbcTemplate.query(
                SELECT_PROSESSERINGS_STATUS +
                        "where prosesserings_status.id in (select distinct id from prosesserings_status where altinn_status = :altinn_status order by id limit :antall) " +
                        "order by melding.id ",
                parameterSource,
                MELDINGS_PROSESSERING_MAPPER);
    }

    public List<MeldingsProsessering> hentMeldingerSomSkalSendesTilAltinn() {
        return hentMedAltinnStatus(AltinnStatus.IKKE_SENDT);
    }

    public List<MeldingsProsessering> hentMeldingerSomSkalSendesTilDokarkiv() {
        return hentMedStatus(AltinnStatus.OK, JoarkStatus.IKKE_SENDT);
    }

    @Transactional
    public void opprett(Melding melding) {
        opprettMelding(melding);
        melding.getVedlegg().forEach(v -> opprettVedlegg(v, melding.getId()));
        melding.getOrganisasjoner().forEach(o -> opprettProsesseringsStatus(o, melding));
    }

    private void opprettProsesseringsStatus(String orgnr, Melding melding) {
        jdbcTemplate.update("Insert into prosesserings_status (" +
                "id, " +
                "melding_id, " +
                "opprettet, " +
                "orgnr, " +
                "altinn_status, " +
                "joark_status) values (" +
                ":id, " +
                ":melding_id, " +
                ":opprettet, " +
                ":orgnr, " +
                ":altinn_status, " +
                ":joark_status)", lagParameterMapForProsesseringsStatus(melding, orgnr));

    }

    private SqlParameterSource lagParameterMapForProsesseringsStatus(Melding melding, String orgnr) {
        return new MapSqlParameterSource()
                .addValue("id", Ulider.nextULID())
                .addValue("melding_id", melding.getId())
                .addValue("opprettet", LocalDateTime.now())
                .addValue("orgnr", orgnr)
                .addValue("altinn_status", AltinnStatus.IKKE_SENDT.name())
                .addValue("joark_status", JoarkStatus.IKKE_SENDT.name());
    }

    private SqlParameterSource lagParameterMapForVedlegg(Vedlegg vedlegg, String meldingId) {
        return new MapSqlParameterSource()
                .addValue("id", vedlegg.getId())
                .addValue("melding_id", meldingId)
                .addValue("opprettet", LocalDateTime.now())
                .addValue("filinnhold", vedlegg.getFilinnhold())
                .addValue("filnavn", vedlegg.getFilnavn())
                .addValue("vedleggnavn", vedlegg.getVedleggnavn());
    }

    private void opprettVedlegg(Vedlegg v, String meldingId) {
        jdbcTemplate.update("Insert into vedlegg (" +
                "id, " +
                "melding_id, " +
                "opprettet, " +
                "filinnhold, " +
                "filnavn, " +
                "vedleggnavn) values (" +
                ":id, " +
                ":melding_id, " +
                ":opprettet, " +
                ":filinnhold, " +
                ":filnavn, " +
                ":vedleggnavn)", lagParameterMapForVedlegg(v, meldingId));
    }

    private void opprettMelding(Melding melding) {
        jdbcTemplate.update("Insert into melding (" +
                "id, " +
                "opprettet, " +
                "orgnr, " +
                "melding, " +
                "tittel, " +
                "system_usercode, " +
                "service_code, " +
                "service_edition, " +
                "tillat_automatisk_sletting_fra_dato, " +
                "tillat_automatisk_sletting_etter_antall_år) values (" +
                ":id, " +
                ":opprettet, " +
                ":orgnr, " +
                ":melding, " +
                ":tittel, " +
                ":system_usercode, " +
                ":service_code, " +
                ":service_edition, " +
                ":tillat_automatisk_sletting_fra_dato, " +
                ":tillat_automatisk_sletting_etter_antall_år)", lagParameterMapForMelding(melding));
    }

    private SqlParameterSource lagParameterMapForMelding(Melding melding) {
        return new MapSqlParameterSource()
                .addValue("id", melding.getId())
                .addValue("opprettet", LocalDateTime.now())
                .addValue("orgnr", String.join(",", melding.getOrganisasjoner()))
                .addValue("melding", melding.getMelding())
                .addValue("tittel", melding.getTittel())
                .addValue("system_usercode", melding.getSystemUsercode())
                .addValue("service_code", melding.getServiceCode())
                .addValue("service_edition", melding.getServiceEdition())
                .addValue("tillat_automatisk_sletting_fra_dato", melding.getTillatAutomatiskSlettingFraDato())
                .addValue("tillat_automatisk_sletting_etter_antall_år", melding.getTillatAutomatiskSlettingEtterAntallÅr());
    }

    public void oppdaterAltinnStatus(String id, AltinnStatus altinnStatus, String altinnReferanse) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("altinn_status", altinnStatus.name())
                .addValue("altinn_referanse", altinnReferanse)
                .addValue("altinn_sendt_tidspunkt", LocalDateTime.now());

        jdbcTemplate.update("update prosesserings_status set altinn_status = :altinn_status, altinn_referanse = :altinn_referanse, altinn_sendt_tidspunkt = :altinn_sendt_tidspunkt where id = :id ", parameterSource);
    }

    public void oppdaterJournalpostId(String id, JoarkStatus joarkStatus, String journalpostId) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("joark_status", joarkStatus.name())
                .addValue("journalpost_id", journalpostId)
                .addValue("joark_sendt_tidspunkt", LocalDateTime.now());

        jdbcTemplate.update("update prosesserings_status set joark_status = :joark_status, journalpost_id = :journalpost_id, joark_sendt_tidspunkt = :joark_sendt_tidspunkt where id = :id ", parameterSource);
    }

}
