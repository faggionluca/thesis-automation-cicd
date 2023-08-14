package com.lucafaggion.thesis.service.interfaces;

import org.springframework.security.core.Authentication;

public interface UserAssociatedAccountService<M, N, R, U> {

  R getUserToken(M tokenRequestMessage);

  U getAuthenticatedUser(R tokenResponse);

  U refreshTokenForUser(U userAssociatedAccount, N tokenRequestMessage);

  void addAssociatedAccountTo(Authentication authentication, U userAssociatedAccount, R tokenResponse);

  void exchangeAndSave(Authentication authentication, M tokenRequestMessage);

}