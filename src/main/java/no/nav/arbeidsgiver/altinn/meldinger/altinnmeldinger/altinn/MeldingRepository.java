package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.AltinnStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Melding;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Vedlegg;
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

    public MeldingRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final ResultSetExtractor<List<Melding>> MELDING_MAPPER =
            JdbcTemplateMapperFactory
                    .newInstance()
                    .addKeys("id", "vedlegg_id")
                    .newResultSetExtractor(Melding.class);

    private static final String SELECT_MELDING = "select " +
            "m.id, " +
            "m.opprettet, " +
            "m.orgnr, " +
            "m.melding, " +
            "m.tittel, " +
            "m.system_usercode, " +
            "m.service_code, " +
            "m.service_edition, " +
            "m.tillat_automatisk_sletting_fra_dato, " +
            "m.tillat_automatisk_sletting_etter_antall_år, " +
            "m.altinn_status, " +
            "m.altinn_referanse, " +
            "m.altinn_sendt_tidspunkt, " +
            "v.id as vedlegg_id, " +
            "v.filinnhold as vedlegg_filinnhold, " +
            "v.filnavn as vedlegg_filnavn, " +
            "v.vedleggnavn as vedlegg_vedleggnavn " +
            "from melding m left join vedlegg v on v.melding_id = m.id ";


    public List<Melding> hent(AltinnStatus altinn_status, int antall) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("altinn_status", altinn_status.name())
                .addValue("antall", antall);

        return jdbcTemplate.query(
                SELECT_MELDING + " " +
                "where altinn_status = :altinn_status " +
                "order by m.id " +
                "limit :antall",
            parameterSource,
            MELDING_MAPPER);
    }

    @Transactional
    public void save(Melding melding) {
        lagreMelding(melding);
        melding.getVedlegg().forEach(v -> lagreVedlegg(v, melding.getId()));
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

    private void lagreVedlegg(Vedlegg v, String meldingId) {
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

    private void lagreMelding(Melding meldingLogg) {
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
                "tillat_automatisk_sletting_etter_antall_år, " +
                "altinn_status) values (" +
                ":id, " +
                ":opprettet, " +
                ":orgnr, " +
                ":melding, " +
                ":tittel, " +
                ":system_usercode, " +
                ":service_code, " +
                ":service_edition, " +
                ":tillat_automatisk_sletting_fra_dato, " +
                ":tillat_automatisk_sletting_etter_antall_år, " +
                ":altinn_status)", lagParameterMapForMelding(meldingLogg));
    }

    private SqlParameterSource lagParameterMapForMelding(Melding meldingLogg) {
        return new MapSqlParameterSource()
                .addValue("id", meldingLogg.getId())
                .addValue("opprettet", meldingLogg.getOpprettet())
                .addValue("orgnr", meldingLogg.getOrgnr())
                .addValue("melding", meldingLogg.getMelding())
                .addValue("tittel", meldingLogg.getTittel())
                .addValue("system_usercode", meldingLogg.getSystemUsercode())
                .addValue("service_code", meldingLogg.getServiceCode())
                .addValue("service_edition", meldingLogg.getServiceEdition())
                .addValue("tillat_automatisk_sletting_fra_dato", meldingLogg.getTillatAutomatiskSlettingFraDato())
                .addValue("tillat_automatisk_sletting_etter_antall_år", meldingLogg.getTillatAutomatiskSlettingEtterAntallÅr())
                .addValue("altinn_status", meldingLogg.getAltinnStatus().name());
    }

    public List<Melding> findAll() {
        return jdbcTemplate.getJdbcTemplate().query(
                SELECT_MELDING +
                "order by m.id ",
                MELDING_MAPPER);
    }

    public void oppdaterAltinnStatus(String id, AltinnStatus altinnStatus, String altinnReferanse) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("altinn_status", altinnStatus.name())
                .addValue("altinn_referanse", altinnReferanse)
                .addValue("altinn_sendt_tidspunkt", LocalDateTime.now());

        jdbcTemplate.update("update melding set altinn_status = :altinn_status, altinn_referanse = :altinn_referanse, altinn_sendt_tidspunkt = :altinn_sendt_tidspunkt where id = :id ", parameterSource);
    }
}
