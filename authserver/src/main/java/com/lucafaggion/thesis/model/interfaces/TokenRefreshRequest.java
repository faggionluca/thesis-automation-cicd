package com.lucafaggion.thesis.model.interfaces;

public interface TokenRefreshRequest {
  public String getGrant_type();
  public String getRefresh_token();
}
