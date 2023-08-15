package com.lucafaggion.thesis.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;

import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.common.model.UserAssociated.bitbucket.BitbucketAccount;
import com.lucafaggion.thesis.common.model.UserAssociated.github.GitHubAccount;
import com.lucafaggion.thesis.model.oauth.OAuthRefreshTokenRequest;
import com.lucafaggion.thesis.model.oauth.OAuthTokenRequest;
import com.lucafaggion.thesis.model.oauth.OAuthTokenResponse;
import com.lucafaggion.thesis.model.oauth.bitbucket.BitBucketTokenRequest;
import com.lucafaggion.thesis.repository.ExternalServiceRepository;
import com.lucafaggion.thesis.repository.UserAssociatedAccountRepository;
import com.lucafaggion.thesis.repository.UserRepository;

@Service
public class BitBucketAssociatedAccountService
    extends AssociatedAccountService<MultiValueMap<String, String>, OAuthRefreshTokenRequest, OAuthTokenResponse, BitbucketAccount> {

  @Value("${com.lucafaggion.oauth.client.bitbucket.client-id}")
  private String bitbucketClientId;
  @Value("${com.lucafaggion.oauth.client.bitbucket.client-secret}")
  private String bitbucketClientSecret;
  @Value("${com.lucafaggion.oauth.client.bitbucket.scopes}")
  private String bitbucketScopes;
  @Value("${com.lucafaggion.oauth.client.bitbucket.uri}")
  private String bitbucketUri;

  public BitBucketAssociatedAccountService(
      UserRepository userRepository,
      ExternalServiceRepository externalServiceRepository,
      UserAssociatedAccountRepository userAssociatedAccountRepository,
      @Value("${com.lucafaggion.oauth.client.bitbucket.access-token-uri}") String tokenUri,
      @Value("${com.lucafaggion.oauth.client.bitbucket.user-uri}") String userUri,
      @Value("${com.lucafaggion.oauth.client.bitbucket.service-name}") String serviceName) {
    super(userRepository,
        externalServiceRepository,
        userAssociatedAccountRepository,
        tokenUri,
        userUri,
        OAuthTokenResponse.class,
        BitbucketAccount.class,
        serviceName);
  }

  public ModelAndView redirectToAuthorize() {
    String uri = String.format(bitbucketUri + "?client_id=%s&response_type=code&state=randomstring", bitbucketClientId);
    return new ModelAndView("redirect:" + uri);
  }

  
  public void exchangeAndSave(Authentication authentication, String code) {
    BitBucketTokenRequest tokenRequest = BitBucketTokenRequest.builder()
        .client_id(bitbucketClientId)
        .client_secret(bitbucketClientSecret)
        .code(code)
        .build();
    MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<String, String>();
    valueMap.put("client_id", Collections.singletonList(tokenRequest.getClient_id()));
    valueMap.put("client_secret", Collections.singletonList(tokenRequest.getClient_secret()));
    valueMap.put("code", Collections.singletonList(tokenRequest.getCode()));
    valueMap.put("grant_type", Collections.singletonList(tokenRequest.getGrant_type()));
    super.exchangeAndSave(authentication, valueMap);
  }

  public UserAssociatedAccount refreshTokenForUser(UserAssociatedAccount userAssociatedAccount) {
    OAuthRefreshTokenRequest refreshTokenRequest = OAuthRefreshTokenRequest.builder()
        .client_id(bitbucketClientId)
        .client_secret(bitbucketClientSecret)
        .grant_type("refresh_token")
        .refresh_token(userAssociatedAccount.getRefresh_token())
        .build();
    super.refreshTokenForUser((BitbucketAccount)userAssociatedAccount, refreshTokenRequest);
    return userAssociatedAccount;
  }


}
