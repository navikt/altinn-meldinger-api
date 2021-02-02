package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@EnableMockOAuth2Server
class AltinnMeldingerApplikasjonTests {

	@Test
	void contextLoads() {
	}

}
