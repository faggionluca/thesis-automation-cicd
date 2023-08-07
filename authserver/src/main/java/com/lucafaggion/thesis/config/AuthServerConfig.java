package com.lucafaggion.thesis.config;

import java.nio.file.attribute.UserPrincipal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
// import com.lucafaggion.thesis.common.model.CustomUserDetails;
import com.lucafaggion.thesis.mixin.CustomUserDetailMixin;
import com.lucafaggion.thesis.model.CustomUserDetails;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class AuthServerConfig {

  @Bean
  public JdbcRegisteredClientRepository registeredClientRepository(JdbcOperations jdbcOperations) {
    // RegisteredClient registeredClient =
    // RegisteredClient.withId(UUID.randomUUID().toString())
    // .clientId("client")
    // .clientSecret("secret")
    // .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
    // .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
    // .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
    // .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
    // .redirectUri("https://oidcdebugger.com/debug")
    // .redirectUri("https://oauthdebugger.com/debug")
    // .redirectUri("https://springone.io/authorized")
    // .scope(OidcScopes.OPENID)
    // .scope("read")
    // .build();
    // JdbcRegisteredClientRepository repo = new
    // JdbcRegisteredClientRepository(jdbcOperations);
    // repo.save(registeredClient);
    return new JdbcRegisteredClientRepository(jdbcOperations);
  }

  @Bean
  @Order(1)
  public SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {

    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(Customizer.withDefaults());
    http.exceptionHandling(e -> e
        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .formLogin(Customizer.withDefaults())
        .authorizeHttpRequests(authz -> authz.anyRequest().authenticated());
    // .sessionManagement(session -> session
    // .sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
    ;
    return http.build();
  }

  // @Bean
  // public JdbcUserDetailsManager userDetailsService(DataSource dataSource) {
  // JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
  // return manager;
  // }

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey = generateRsa();
    JWKSet jwkSet = new JWKSet((JWK) rsaKey);
    return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
  }

  // @Bean
  // public OAuth2AuthorizationService authorizationService(JdbcOperations jdbcOperations,
  //     RegisteredClientRepository registeredClientRepository) {
  //   return new JdbcOAuth2AuthorizationService(jdbcOperations,
  //       registeredClientRepository);
  // }

  // //
  // //
  // //https://github.com/spring-projects/spring-authorization-server/issues/397#issuecomment-900148920
  // @Bean
  // public OAuth2AuthorizationService authorizationService(JdbcOperations
  // jdbcOperations,
  // RegisteredClientRepository registeredClientRepository) {
  // JdbcOAuth2AuthorizationService service = new
  // JdbcOAuth2AuthorizationService(jdbcOperations,
  // registeredClientRepository);
  // JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper = new
  // JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(
  // registeredClientRepository);

  // ObjectMapper objectMapper = new ObjectMapper();
  // ClassLoader classLoader =
  // JdbcOAuth2AuthorizationService.class.getClassLoader();
  // List<com.fasterxml.jackson.databind.Module> securityModules =
  // SecurityJackson2Modules.getModules(classLoader);
  // objectMapper.registerModules(securityModules);
  // objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
  // // You will need to write the Mixin for your class so Jackson can marshall
  // objectMapper.addMixIn(CustomUserDetails.class, CustomUserDetailMixin.class);
  // rowMapper.setObjectMapper(objectMapper);
  // service.setAuthorizationRowMapper(rowMapper);
  // return service;
  // }

  @Bean
  public OAuth2AuthorizationConsentService authorizationConsentService(
  JdbcOperations jdbcOperations,
  RegisteredClientRepository registeredClientRepository) {
  return new JdbcOAuth2AuthorizationConsentService(jdbcOperations,
  registeredClientRepository);
  }

  private static RSAKey generateRsa() {
    KeyPair keyPair = generateRsaKey();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    return new RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .keyID(UUID.randomUUID().toString())
        .build();
  }

  private static KeyPair generateRsaKey() {
    KeyPair keyPair;
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      keyPair = keyPairGenerator.generateKeyPair();
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
    return keyPair;
  }
}
