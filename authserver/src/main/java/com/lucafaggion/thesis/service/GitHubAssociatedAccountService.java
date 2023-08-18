package com.lucafaggion.thesis.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.common.model.UserAssociated.github.GitHubAccount;
import com.lucafaggion.thesis.model.oauth.OAuthRefreshTokenRequest;
import com.lucafaggion.thesis.model.oauth.OAuthTokenRequest;
import com.lucafaggion.thesis.model.oauth.OAuthTokenResponse;
import com.lucafaggion.thesis.repository.ExternalServiceRepository;
import com.lucafaggion.thesis.repository.UserAssociatedAccountRepository;
import com.lucafaggion.thesis.repository.UserRepository;

@Service
public class GitHubAssociatedAccountService
    extends AssociatedAccountService<OAuthTokenRequest, OAuthRefreshTokenRequest, OAuthTokenResponse, GitHubAccount> {

  @Value("${com.lucafaggion.oauth.client.github.client-id}") 
  private String githubClientId;
  @Value("${com.lucafaggion.oauth.client.github.client-secret}")
  private String githubClientSecret;
  @Value("${com.lucafaggion.oauth.client.github.scopes}")
  private String githubScopes;
  @Value("${com.lucafaggion.oauth.client.github.uri}")
  private String githubUri;

  public GitHubAssociatedAccountService(
      UserRepository userRepository,
      ExternalServiceRepository externalServiceRepository,
      UserAssociatedAccountRepository userAssociatedAccountRepository,
      @Value("${com.lucafaggion.oauth.client.github.access-token-uri}") String githubAccessTokenUri,
      @Value("${com.lucafaggion.oauth.client.github.service-name}") String githubServiceName,
      @Value("${com.lucafaggion.oauth.client.github.user-uri}") String githubUserUri) {
    super(userRepository,
        externalServiceRepository,
        userAssociatedAccountRepository,
        githubAccessTokenUri,
        githubUserUri,
        OAuthTokenResponse.class,
        GitHubAccount.class,
        githubServiceName);
  }

  @Override
  public ModelAndView redirectToAuthorize() {
    String uri = String.format(githubUri + "?client_id=%s&scope=%s&state=randomstring", githubClientId, githubScopes);
    return new ModelAndView("redirect:" + uri);
  }

  public void exchangeAndSave(Authentication authentication, String code) {
    OAuthTokenRequest gitHubTokenExchange = OAuthTokenRequest.builder()
        .client_id(githubClientId)
        .client_secret(githubClientSecret)
        .code(code)
        .build();
    super.exchangeAndSave(authentication, gitHubTokenExchange);
  }

  @Override
  public UserAssociatedAccount refreshTokenFor(UserAssociatedAccount userAssociatedAccount) {
    OAuthRefreshTokenRequest gitHubRefreshTokenExchange = OAuthRefreshTokenRequest.builder()
        .client_id(githubClientId)
        .client_secret(githubClientSecret)
        .grant_type("refresh_token")
        .refresh_token(userAssociatedAccount.getRefresh_token())
        .build();
    super.refreshTokenForUser((GitHubAccount)userAssociatedAccount, gitHubRefreshTokenExchange);
    return userAssociatedAccount;
  }

  @Override
  protected HttpHeaders buildTokenRequestHeaders(HttpHeaders headers, OAuthTokenRequest tokenRequestMessage) {
    return headers;
  }

  @Override
  protected HttpHeaders buildRefreshTokenRequestHeaders(HttpHeaders headers,
      OAuthRefreshTokenRequest tokenRequestMessage) {
    return headers;
  }

  @Override
  protected long defaultTokenValidity() {
    return 157784630000L; // non scadono mai (5 anni forziamo il refresh)
  }

  @Override
  protected long defaultRefreshTokenValidity() {
    return 157784630000L; // non scadono mai (5 anni forziamo il refresh))
  }

}
