package com.lucafaggion.thesis.model.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubTokenResponse {
  private String access_token;
  private String scope;
  private String token_type;
}
