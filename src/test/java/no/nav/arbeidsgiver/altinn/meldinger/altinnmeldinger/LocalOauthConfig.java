package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.OAuth2Config;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import no.nav.security.mock.oauth2.token.OAuth2TokenProvider;
import no.nav.security.token.support.core.configuration.ProxyAwareResourceRetriever;
import no.nav.security.token.support.spring.test.MockLoginController;
import no.nav.security.token.support.spring.test.MockOAuth2ServerAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;

@Configuration
@Import(MockLoginController.class)
@Profile("local")
public class LocalOauthConfig {

    private final Logger log = LoggerFactory.getLogger(MockOAuth2ServerAutoConfiguration.class);
    private final MockOAuth2Server mockOAuth2Server;

    public LocalOauthConfig(@Value("${tilgangskontroll.group}") String group) {
        DefaultOAuth2TokenCallback callback = new DefaultOAuth2TokenCallback(
                "aad",
                UUID.randomUUID().toString(),
                List.of("altinn-meldinger-api"),
                Map.of("groups", List.of(group)),
                3600L
        );

        this.mockOAuth2Server = new MockOAuth2Server(
                new OAuth2Config(
                        false,
                        new OAuth2TokenProvider(),
                        Set.of(callback)
                )
        );
    }

    @Bean
    @Primary
    @DependsOn("mockOAuth2Server")
    ProxyAwareResourceRetriever overrideOidcResourceRetriever() {
        return new ProxyAwareResourceRetriever();
    }

    @Bean
    MockOAuth2Server mockOAuth2Server() {
        return mockOAuth2Server;
    }

    @PostConstruct
    void start() {
        try {
            int port = 9000;
            log.debug("starting mock oauth2 server on port " + port);
            mockOAuth2Server.start(port);
        } catch (IOException e) {
            log.error("could not register and start MockOAuth2Server");
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    void shutdown() throws IOException {
        log.debug("shutting down the mock oauth2 server.");
        mockOAuth2Server.shutdown();
    }
}
