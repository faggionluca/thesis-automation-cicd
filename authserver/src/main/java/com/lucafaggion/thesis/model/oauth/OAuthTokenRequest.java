package com.lucafaggion.thesis.model.oauth;

import com.lucafaggion.thesis.model.interfaces.TokenRequest;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthTokenRequest implements TokenRequest {
  @NonNull
  private String client_id;
  @NonNull
  private String client_secret;
  @NonNull
  private String code;
}
