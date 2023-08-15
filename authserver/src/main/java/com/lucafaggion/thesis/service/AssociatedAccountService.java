package com.lucafaggion.thesis.service;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;

import com.lucafaggion.thesis.common.model.ExternalService;
import com.lucafaggion.thesis.common.model.User;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.model.CustomUserDetails;
import com.lucafaggion.thesis.model.interfaces.TokenRefreshRequest;
import com.lucafaggion.thesis.model.interfaces.TokenRequest;
import com.lucafaggion.thesis.model.interfaces.TokenResponse;
import com.lucafaggion.thesis.repository.ExternalServiceRepository;
import com.lucafaggion.thesis.repository.UserAssociatedAccountRepository;
import com.lucafaggion.thesis.repository.UserRepository;
import com.lucafaggion.thesis.service.exceptions.RefreshTokenExpiredException;
import com.lucafaggion.thesis.service.interfaces.UserAssociatedAccountService;

public class AssociatedAccountService<M, N, R extends TokenResponse, U extends UserAssociatedAccount> implements UserAssociatedAccountService<M, N, R, U> {

  private final static Logger logger = LoggerFactory.getLogger(AssociatedAccountService.class);
  public final RestTemplate restTemplate;
  private final UserRepository userRepository;
  private final ExternalServiceRepository externalServiceRepository;
  private final UserAssociatedAccountRepository userAssociatedAccountRepository;
  private final String tokenUri;
  private final String userUri;
  private final Class<R> tokenResponseType;
  private final Class<U> userResponseType;
  private final String serviceName;

  public AssociatedAccountService(
      UserRepository userRepository,
      ExternalServiceRepository externalServiceRepository,
      UserAssociatedAccountRepository userAssociatedAccountRepository,
      String tokenUri,
      String userUri,
      Class<R> tokenResponseType,
      Class<U> userResponseType,
      String serviceName
    ) {
    this.restTemplate = new RestTemplate();
    this.userRepository = userRepository;
    this.externalServiceRepository = externalServiceRepository;
    this.userAssociatedAccountRepository = userAssociatedAccountRepository;
    this.tokenUri = tokenUri;
    this.userUri = userUri;
    this.tokenResponseType = tokenResponseType;
    this.userResponseType = userResponseType;
    this.serviceName = serviceName;
  }

  @Override
  public R getUserToken(M tokenRequestMessage) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    // Creaiamo la richiesta
    HttpEntity<M> request = new HttpEntity<M>(tokenRequestMessage, headers);
    // eseguiamo la richesta
    ResponseEntity<R> response = this.restTemplate.exchange(this.tokenUri, HttpMethod.POST, request, this.tokenResponseType);

    if (response.getStatusCode() == HttpStatus.OK) {
      return response.getBody();
    }
    return null;
  }

  @Override
  public U getAuthenticatedUser(R tokenResponse) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setBearerAuth(tokenResponse.getAccess_token());
    // Creaiamo la richiesta
    HttpEntity<Object> request = new HttpEntity<Object>(null, headers);
    // eseguiamo la richesta
    ResponseEntity<U> response = this.restTemplate.exchange(this.userUri, HttpMethod.GET, request, this.userResponseType);
    if (response.getStatusCode() == HttpStatus.OK) {
      return response.getBody();
    }
    return null;
  }

  @Override
  public void addAssociatedAccountTo(Authentication authentication, U userAssociatedAccount, R tokenResponse) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    ExternalService service = externalServiceRepository.findByName(this.serviceName).orElseThrow();

    userAssociatedAccount.setToken(tokenResponse.getAccess_token());
    userAssociatedAccount.setRefresh_token(tokenResponse.getAccess_token());
    
    if (tokenResponse.getExpires_in() != null) {
      Instant validUntil = Instant.now().plusSeconds(tokenResponse.getExpires_in());
      userAssociatedAccount.setToken_valid_until(new Date(validUntil.toEpochMilli()));
    }
    Instant refreshValidUntil = Instant.now().plusSeconds(15638400); // valido per 6 mesi di base
    if (tokenResponse.getRefresh_token_expires_in() != null) {
      refreshValidUntil = Instant.now().plusSeconds(tokenResponse.getRefresh_token_expires_in());
    }
    userAssociatedAccount.setRefresh_token_valid_until(new Date(refreshValidUntil.toEpochMilli()));

    userAssociatedAccount.setService(service);
    user.getUserAssociatedAccounts().add(userAssociatedAccount);
    this.userRepository.save(user);
  }

  @Override
  public U refreshTokenForUser(U userAssociatedAccount, N tokenRequestMessage) {
    Date current = new Date();
    // Se il refresh token valid time e prima della data attuale lanciamo un errore
    if (userAssociatedAccount.getRefresh_token_valid_until().before(current)) {
      throw new RefreshTokenExpiredException();
    }

    // Se il token valid time e prima della data attuale dobbiamo generarne un altro utilizzando il refresh token
    if (userAssociatedAccount.getToken_valid_until().before(current)) {


      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      // Creaiamo la richiesta
      HttpEntity<N> request = new HttpEntity<N>(tokenRequestMessage, headers);
      // eseguiamo la richesta
      ResponseEntity<R> response = this.restTemplate.exchange(this.tokenUri, HttpMethod.POST, request, this.tokenResponseType);
      if (response.getStatusCode() == HttpStatus.OK) {
        R tokenResponse = response.getBody();

        Instant validUntil = Instant.now().plusSeconds(tokenResponse.getExpires_in());
        Instant refreshValidUntil = Instant.now().plusSeconds(tokenResponse.getRefresh_token_expires_in());

        userAssociatedAccount.setToken(tokenResponse.getAccess_token());
        userAssociatedAccount.setRefresh_token(tokenResponse.getAccess_token());
        userAssociatedAccount.setToken_valid_until(new Date(validUntil.toEpochMilli()));
        userAssociatedAccount.setRefresh_token_valid_until(new Date(refreshValidUntil.toEpochMilli()));
        this.userAssociatedAccountRepository.save(userAssociatedAccount);

      }
    }
    return userAssociatedAccount;
  }

  @Override
  public void exchangeAndSave(Authentication authentication, M tokenRequestMessage) {
    
    R tokenResponse = this.getUserToken(tokenRequestMessage);
    if (tokenResponse != null) {
      U associatedAccount = this.getAuthenticatedUser(tokenResponse);
      if (associatedAccount != null) {
        this.addAssociatedAccountTo(authentication, associatedAccount, tokenResponse);
      }
    }
  }
}
