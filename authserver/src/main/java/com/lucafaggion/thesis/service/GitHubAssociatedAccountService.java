package com.lucafaggion.thesis.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.common.model.UserAssociated.github.GitHubAccount;
import com.lucafaggion.thesis.model.github.GitHubRefreshTokenExchange;
import com.lucafaggion.thesis.model.github.GitHubTokenExchange;
import com.lucafaggion.thesis.model.github.GitHubTokenResponse;
import com.lucafaggion.thesis.repository.ExternalServiceRepository;
import com.lucafaggion.thesis.repository.UserAssociatedAccountRepository;
import com.lucafaggion.thesis.repository.UserRepository;

@Service
public class GitHubAssociatedAccountService
    extends AssociatedAccountService<GitHubTokenExchange, GitHubRefreshTokenExchange, GitHubTokenResponse, GitHubAccount> {

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
        GitHubTokenResponse.class,
        GitHubAccount.class,
        githubServiceName);
  }

  public ModelAndView redirectToAuthorize() {
    String uri = String.format(githubUri + "?client_id=%s&scope=%s&state=randomstring", githubClientId, githubScopes);
    return new ModelAndView("redirect:" + uri);
  }

  public void exchangeAndSave(Authentication authentication, String code) {
    GitHubTokenExchange gitHubTokenExchange = GitHubTokenExchange.builder()
        .client_id(githubClientId)
        .client_secret(githubClientSecret)
        .code(code)
        .build();
    super.exchangeAndSave(authentication, gitHubTokenExchange);
  }

  public UserAssociatedAccount refreshTokenForUser(UserAssociatedAccount userAssociatedAccount) {
    GitHubRefreshTokenExchange gitHubRefreshTokenExchange = GitHubRefreshTokenExchange.builder()
        .client_id(githubClientId)
        .client_secret(githubClientSecret)
        .grant_type("refresh_token")
        .refresh_token(userAssociatedAccount.getRefresh_token())
        .build();
    super.refreshTokenForUser((GitHubAccount)userAssociatedAccount, gitHubRefreshTokenExchange);
    return userAssociatedAccount;
  }

}
