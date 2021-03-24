package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;

import java.util.Arrays;
import java.util.Collections;

public class TestUtils {
    public static String token(MockOAuth2Server mockOAuth2Server, String issuerId, String subject, String audience, String... groups) {
        return mockOAuth2Server.issueToken(
                issuerId,
                "theclientid",
                new DefaultOAuth2TokenCallback(
                        issuerId,
                        subject,
                        audience,
                        Collections.singletonMap("groups", Arrays.asList(groups)),
                        3600
                )
        ).serialize();
    }
}
