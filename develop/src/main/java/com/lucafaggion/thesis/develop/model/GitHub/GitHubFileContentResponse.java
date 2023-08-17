package com.lucafaggion.thesis.develop.model.GitHub;

import lombok.Data;

@Data
public class GitHubFileContentResponse {
  public String name;
  public String path;
  public String sha;
  public int size;
  public String url;
  public String html_url;
  public String git_url;
  public String download_url;
  public String type;
  public String content;
  public String encoding;
}
