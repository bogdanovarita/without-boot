package ru.example.config;

import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.RequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import ru.example.client.TestFeignClient;

import java.util.Collection;
import java.util.Collections;

@Configuration
@PropertySource(value = "classpath:/application.properties")
public class FeignConfig {

    @Bean
    public TestFeignClient testFeignClient(@Value("${feign.client.test.url}") String url,
                                           RequestInterceptor testRequestInterceptor) {
        return Feign.builder()
                .contract(new Contract.Default())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Logger.ErrorLogger())
                .logLevel(Logger.Level.FULL)
                .requestInterceptor(testRequestInterceptor)
                .target(TestFeignClient.class, url);
    }

    @Bean
    public RequestInterceptor testRequestInterceptor(ClientRegistration testClientRegistration,
                                                     OAuth2AuthorizedClientManager manager) {
        return requestTemplate -> {
            OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                    .withClientRegistrationId(testClientRegistration.getRegistrationId())
                    .principal(createPrincipal(testClientRegistration))
                    .build();

            OAuth2AuthorizedClient client = manager.authorize(request);
            if (client != null) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());
            }
        };
    }

    private Authentication createPrincipal(ClientRegistration clientRegistration) {
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptySet();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return this;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public String getName() {
                return clientRegistration.getClientId();
            }
        };
    }
}
