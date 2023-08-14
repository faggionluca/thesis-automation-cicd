package com.lucafaggion.thesis.common.model.UserAssociated.github;

import java.util.Date;

import com.lucafaggion.thesis.common.model.UserAssociatedAccount;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class GitHubAccount extends UserAssociatedAccount {
  public String login;

  public void setLogin(String login) {
    this.login = login;
    this.setUsername(login);
  }

  public String node_id;
  public String avatar_url;
  public String gravatar_id;
  public String url;
  public String html_url;
  public String followers_url;
  public String following_url;
  public String gists_url;
  public String starred_url;
  public String subscriptions_url;
  public String organizations_url;
  public String repos_url;
  public String events_url;
  public String received_events_url;
  public String type;
  public boolean site_admin;
  public String name;
  public String company;
  public String blog;
  public String location;
  public String email;
  public boolean hireable;
  public String bio;
  public String twitter_username;
  public int public_repos;
  public int public_gists;
  public int followers;
  public int following;
  public Date created_at;
  public Date updated_at;
  public int private_gists;
  public int total_private_repos;
  public int owned_private_repos;
  public int disk_usage;
  public int collaborators;
  public boolean two_factor_authentication;
}
