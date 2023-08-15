package com.lucafaggion.thesis.model.oauth.bitbucket;

import com.lucafaggion.thesis.model.oauth.OAuthTokenRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BitBucketTokenRequest extends OAuthTokenRequest {
  @Builder.Default
  private String grant_type = "authorization_code";
}
