package com.lucafaggion.thesis.develop.model.GitHub;

import lombok.Data;

@Data
public class GitHubPushEvent {

  private String ref;
  private String before;
  private String after;
  private GitHubRepository repository;
  private GitHubUser pusher;
  private GitHubUser sender;
  private boolean created;
  private boolean deleted;
  private boolean forced;

}
