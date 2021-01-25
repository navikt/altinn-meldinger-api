package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import de.huxhorn.sulky.ulid.ULID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeldingLoggRepository extends JpaRepository<MeldingLogg, ULID> {
}
