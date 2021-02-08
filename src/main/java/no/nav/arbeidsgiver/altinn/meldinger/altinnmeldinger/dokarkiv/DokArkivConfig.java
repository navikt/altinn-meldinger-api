package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Configuration
@EnableOAuth2Client(cacheEnabled = true)
public class DokArkivConfig {
    private final String uri;
    private String pdfGenUri;

    public DokArkivConfig(@Value("${dokarkiv.uri}") String uri, @Value("${pdfgen.uri}") String pdfGenUri) {
        this.uri = uri;
        this.pdfGenUri = pdfGenUri;
    }

    public String getUri() {
        return uri;
    }

    public String getPdfGenUri() {
        return pdfGenUri;
    }

    //@Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    RestTemplate restTemplateOauth2(
            RestTemplateBuilder restTemplateBuilder,
            ClientConfigurationProperties clientConfigurationProperties,
            OAuth2AccessTokenService oAuth2AccessTokenService
    ) {
        ClientProperties clientProperties =
                Optional.ofNullable(clientConfigurationProperties.getRegistration().get("altinnmelding"))
                        .orElseThrow(() -> new RuntimeException("could not find oauth2 client config for altinnmelding"));

        return restTemplateBuilder
                .additionalInterceptors(bearerTokenInterceptor(clientProperties, oAuth2AccessTokenService))
                .build();
    }

    private ClientHttpRequestInterceptor bearerTokenInterceptor(
            ClientProperties clientProperties,
            OAuth2AccessTokenService oAuth2AccessTokenService
    ) {
        return (request, body, execution) -> {
            OAuth2AccessTokenResponse response =
                    oAuth2AccessTokenService.getAccessToken(clientProperties);
            request.getHeaders().setBearerAuth(response.getAccessToken());
            return execution.execute(request, body);
        };
    }
}
