package com.lucafaggion.thesis.mixin;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucafaggion.thesis.model.User;

public abstract class CustomUserDetailMixin {
  @JsonProperty
  Collection<? extends GrantedAuthority> authorities;
  @JsonProperty
  String password;
  @JsonProperty
  String username;
  @JsonProperty
  boolean accountNonExpired;
  @JsonProperty
  boolean accountNonLocked;
  @JsonProperty
  boolean credentialsNonExpired;
  @JsonProperty
  boolean enabled;
}
