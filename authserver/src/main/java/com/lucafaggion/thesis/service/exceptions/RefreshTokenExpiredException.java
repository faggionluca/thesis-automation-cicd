package com.lucafaggion.thesis.service.exceptions;

public class RefreshTokenExpiredException extends RuntimeException {

  public RefreshTokenExpiredException() {
    super("The refresh token is expired, user has to re-authorize");
  }
}
