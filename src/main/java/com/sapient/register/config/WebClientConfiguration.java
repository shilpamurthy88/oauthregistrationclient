
package com.sapient.register.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebClientConfiguration {

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                                 OAuth2AuthorizedClientService authorizedClientService) {
        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .password()
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        Map<String, Object> passwordAttributes = new HashMap<>();
        passwordAttributes.put(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, "shilpa22@gmail.com");
        passwordAttributes.put(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, "Shi22@123");

        authorizedClientManager.setContextAttributesMapper(request -> passwordAttributes);

        return authorizedClientManager;
    }

    @Bean
    public WebClient webClient(@Qualifier("authorizedClientManager") OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oAuth2Filer = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oAuth2Filer.setDefaultClientRegistrationId("authProvider");
        return WebClient.builder()
                .filter(oAuth2Filer)
                .build();
    }

}

