package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.token.support.spring.test.MockLoginController;

public class MockLoginController2 extends MockLoginController {
    public MockLoginController2(MockOAuth2Server mockOAuth2Server) {
        super(mockOAuth2Server);
    }
}
