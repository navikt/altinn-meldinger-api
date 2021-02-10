package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Configuration
@EnableOAuth2Client(cacheEnabled = true)
public class RestTemplateConfig {

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer) {
        return configurer.configure(new RestTemplateBuilder()).setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5));
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .customizers(restTemplate -> restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory()))
                .additionalMessageConverters(
                        new StringHttpMessageConverter(StandardCharsets.UTF_8),
                        new ByteArrayHttpMessageConverter())
                .build();
    }

    @Bean
    RestTemplate restempateOauth2(
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
