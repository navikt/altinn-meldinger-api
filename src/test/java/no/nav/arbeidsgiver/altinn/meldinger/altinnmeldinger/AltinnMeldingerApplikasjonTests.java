package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.profiles.active=test"})
@EnableMockOAuth2Server
class AltinnMeldingerApplikasjonTests {

	@Test
	void contextLoads() {
	}

}
