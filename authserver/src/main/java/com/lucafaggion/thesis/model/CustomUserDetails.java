package com.lucafaggion.thesis.model;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lucafaggion.thesis.common.model.User;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@RequiredArgsConstructor
@Builder
@Jacksonized
public class CustomUserDetails implements UserDetails {

  private static final long serialVersionUID = 1L;

  // private final User user;
  private final String password;
  private final String username;
  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean enabled;
  private final boolean credentialsNonExpired;
  private final Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(User user) {
    this.password = user.getPassword();
    this.username = user.getUsername();
    this.accountNonExpired = user.getAccountNonExpired();
    this.accountNonLocked = user.getAccountNonLocked();
    this.enabled = user.getEnabled();
    this.authorities = user.getAuthorities().stream()
        .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
        .collect(Collectors.toSet());
    this.credentialsNonExpired = user.getCredentialsNonExpired();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}