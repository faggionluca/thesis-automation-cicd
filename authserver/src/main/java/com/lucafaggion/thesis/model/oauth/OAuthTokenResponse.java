package com.lucafaggion.thesis.model.oauth;

import com.lucafaggion.thesis.model.interfaces.TokenResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthTokenResponse implements TokenResponse{
  private String access_token;
  private String scope;
  private String token_type;
  private String refresh_token;
  private Integer expires_in;
  private Integer refresh_token_expires_in;
}
