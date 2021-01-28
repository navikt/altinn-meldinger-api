package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.AltinnStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.MeldingLogg;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class MeldingLoggRepository {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public MeldingLoggRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MeldingLogg> hentAltinnMeldinger(AltinnStatus status, int antall) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("status", status);

        return jdbcTemplate.query("select " +
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
                "status from melding_logg " +
                "where status = :status",
                parameterSource,
                (resultSet, i) -> getMeldingLogg(resultSet));
    }

    public void save(MeldingLogg meldingLogg) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
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
                .addValue("status", meldingLogg.getAltinnStatus().name());
        jdbcTemplate.update("Insert into melding_logg (" +
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
                "status) values (" +
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
                ":status)", parameterSource);

    }

    public List<MeldingLogg> findAll() {

        return jdbcTemplate.getJdbcTemplate().query("select " +
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
                "status from melding_logg", (resultSet, i) -> getMeldingLogg(resultSet));
    }

    private MeldingLogg getMeldingLogg(ResultSet resultSet) {
        try {
            return new MeldingLogg(
                        resultSet.getTimestamp("opprettet").toLocalDateTime(),
                        resultSet.getString("id"),
                        resultSet.getString("orgnr"),
                        resultSet.getString("melding"),
                        resultSet.getString("tittel"),
                        resultSet.getString("system_usercode"),
                        resultSet.getString("service_code"),
                        resultSet.getString("service_edition"),
                        Optional.ofNullable(resultSet.getTimestamp("tillat_automatisk_sletting_fra_dato")).map(d -> d.toLocalDateTime()).orElse(null),
                        resultSet.getInt("tillat_automatisk_sletting_etter_antall_år"),
                        AltinnStatus.valueOf(resultSet.getString("status"))
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    public void oppdaterStatus(String id, AltinnStatus status) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("status", status);

        jdbcTemplate.update("update melding_logg set status = :status where id = :id ", parameterSource);
    }
}
