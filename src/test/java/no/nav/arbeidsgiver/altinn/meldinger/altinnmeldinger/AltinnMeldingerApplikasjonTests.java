package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EnableMockOAuth2Server
class AltinnMeldingerApplikasjonTests {

	@Test
	void contextLoads() {
	}

}
