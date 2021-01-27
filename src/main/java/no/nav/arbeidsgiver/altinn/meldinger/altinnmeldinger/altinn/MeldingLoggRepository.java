package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

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

    /*
        private static Virksomhetsklassifikasjon mapTilVirksomhetsklassifikasjon
            (ResultSet rs, Klassifikasjonskilde klassifikasjonskilde) throws SQLException {
        switch (klassifikasjonskilde) {
            case SEKTOR:
                return new Sektor(rs.getString(KODE), rs.getString(NAVN));
            case NÆRING:
                return new Næring(rs.getString(KODE), rs.getString(NAVN));
            default:
                throw new IllegalArgumentException();
        }
    }
     */
}
