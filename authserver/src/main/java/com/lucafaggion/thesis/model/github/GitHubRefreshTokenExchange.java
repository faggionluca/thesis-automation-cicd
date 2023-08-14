package com.lucafaggion.thesis.model.github;

import com.lucafaggion.thesis.model.interfaces.TokenRefreshRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubRefreshTokenExchange implements TokenRefreshRequest {
  @NonNull
  private String client_id;
  @NonNull
  private String client_secret;
  @NonNull
  private String grant_type;
  @NonNull
  private String refresh_token;
}