package com.lucafaggion.thesis.model.interfaces;

public interface TokenResponse {
  public String getAccess_token();
  public String getRefresh_token();
  public Integer getExpires_in(); 
  public Integer getRefresh_token_expires_in(); 

}
