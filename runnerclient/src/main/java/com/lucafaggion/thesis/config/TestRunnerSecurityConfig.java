package com.lucafaggion.thesis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TestRunnerSecurityConfig {

  @Value("${security.oauth2.client.client-id}")
  private String clientID;
  @Value("${security.oauth2.client.client-secret}")
  private String clientSecret;
  @Value("${security.oauth2.client.access-token-uri}")
  private String accessTokenUri;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.oauth2Client(Customizer.withDefaults());
    http.authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
    return http.build();
  }

  @Bean
  public OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository auth2AuthorizedClientRepository) {
    OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
        .authorizationCode()
        .refreshToken()
        .clientCredentials()
        .build();

    DefaultOAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
        clientRegistrationRepository, auth2AuthorizedClientRepository);
    defaultOAuth2AuthorizedClientManager.setAuthorizedClientProvider(provider);

    return defaultOAuth2AuthorizedClientManager;
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository() {
    ClientRegistration c1 = ClientRegistration.withRegistrationId("1")
        .clientId(clientID)
        .clientSecret(clientSecret)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .tokenUri(accessTokenUri)
        .scope(OidcScopes.OPENID)
        .scope("read")
        .build();

    InMemoryClientRegistrationRepository clientRegistrationRepository = new InMemoryClientRegistrationRepository(c1);

    return clientRegistrationRepository;
  }
}
