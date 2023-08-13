package com.lucafaggion.thesis.model.github;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubTokenExchange {
  @NonNull
  private String client_id;
  @NonNull
  private String client_secret;
  @NonNull
  private String code;
}
